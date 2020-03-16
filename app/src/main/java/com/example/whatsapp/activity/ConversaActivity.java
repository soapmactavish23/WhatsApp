package com.example.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.whatsapp.Adapter.MensagemAdapter;
import com.example.whatsapp.R;
import com.example.whatsapp.config.ConfiguracaoFirebase;
import com.example.whatsapp.helper.Base64Custom;
import com.example.whatsapp.helper.Preferencias;
import com.example.whatsapp.model.Conversa;
import com.example.whatsapp.model.Mensagem;
import com.google.android.gms.common.api.BooleanResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConversaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText txtMensagem;
    private ImageButton btnEnviar;
    private DatabaseReference fireBase;
    private ListView listView;
    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter adapter;
    private ValueEventListener valueMensagem;

    //Dados do Destinatario
    private String nomeUsuarioDestinatario;
    private String idUsuarioDestinatario;

    //Dados do Remetente
    private String idUsuarioRementente;
    private String nomeUsuarioRemetente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = (Toolbar) findViewById(R.id.tb_conversa);
        txtMensagem = (EditText) findViewById(R.id.txtMensagem);
        btnEnviar = (ImageButton) findViewById(R.id.btnEnviar);
        listView = (ListView) findViewById(R.id.lv_conversas);

        //Recuperar dados do usuario logado
        Preferencias preferencias = new Preferencias(ConversaActivity.this);
        idUsuarioRementente = preferencias.getIdentificador();
        nomeUsuarioRemetente = preferencias.getNOME();

        Bundle extra = getIntent().getExtras();

        if(extra != null){
            nomeUsuarioDestinatario = extra.getString("nome");
            String emailDestinatario = extra.getString("email");
            idUsuarioDestinatario = Base64Custom.codificarBase64(emailDestinatario);
        }

        //Configuracao da toolbar
        toolbar.setTitle(nomeUsuarioDestinatario);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConversaActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //Montar o listView e adapter
        mensagens = new ArrayList<>();
        adapter = new MensagemAdapter(getApplicationContext(), mensagens);
        listView.setAdapter(adapter);

        //Recuperar as mensagens
        fireBase = ConfiguracaoFirebase.getFireBase().
                child("mensagens").
                child(idUsuarioRementente).
                child(idUsuarioDestinatario);

        //Criar listener para msg
        valueMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //Limpar mensagens
                mensagens.clear();

                //Recuperar msgs
                for(DataSnapshot dados: dataSnapshot.getChildren()){
                    Mensagem mensagem = dados.getValue(Mensagem.class);
                    mensagens.add(mensagem);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        fireBase.addValueEventListener(valueMensagem);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textMensagem = txtMensagem.getText().toString();

                if (textMensagem.isEmpty()){
                    Toast.makeText(ConversaActivity.this, "Digite uma mensagem", Toast.LENGTH_SHORT).show();
                }else{

                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioRementente);
                    mensagem.setMensagem(textMensagem);

                    //Salvamos a mensagem para o remetente
                    Boolean retornoRemetente = salvarMensagem(idUsuarioRementente, idUsuarioDestinatario, mensagem);
                    if(! retornoRemetente){
                        Toast.makeText(
                                ConversaActivity.this,
                                "Problema ao enviar mensagem para o destinatario, tente novamente!",
                                Toast.LENGTH_SHORT).show();
                    }else{

                        //Salvamos a mensagem para o destinatario
                        Boolean retornoDestinatario = salvarMensagem(idUsuarioDestinatario, idUsuarioRementente, mensagem);
                        if(!retornoDestinatario){
                            Toast.makeText(
                                    ConversaActivity.this,
                                    "Problema ao enviar mensagem para o destinatario, tente novamente!",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }

                    //Salvamos a conversa para o remetente
                    Conversa conversa = new Conversa();
                    conversa.setIdUsuario(idUsuarioDestinatario);
                    conversa.setNome(nomeUsuarioDestinatario);
                    conversa.setMensagem(textMensagem);
                    Boolean retornoConversaRemetente = salvarConversa(idUsuarioRementente, idUsuarioDestinatario, conversa);
                    if(!retornoConversaRemetente){
                        Toast.makeText(ConversaActivity.this,
                                "Problema ao salvar conversa, tente novamente!",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        //Salvamos a conversa para o destinatario
                        Conversa conversaDest = new Conversa();
                        conversaDest.setIdUsuario( nomeUsuarioRemetente );
                        conversaDest.setNome(textMensagem);
                        Boolean retornoConversaDestinatario = salvarConversa(idUsuarioDestinatario, idUsuarioRementente, conversaDest);
                        if(!retornoConversaDestinatario){
                            Toast.makeText(ConversaActivity.this,
                                    "Problema ao salvar conversa para o destinatário, tente novamente",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    txtMensagem.setText("");

                }
            }
        });

    }

    private boolean salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem){
        try{

            fireBase = ConfiguracaoFirebase.getFireBase().child("mensagens");
            fireBase.child(idRemetente).child(idDestinatario).push().setValue(mensagem);

            return true;

        }catch (Exception e){
            e.printStackTrace();

            return false;
        }
    }

    private boolean salvarConversa(String idRemetente, String idDestinatario, Conversa conversa){
        try{

            fireBase = ConfiguracaoFirebase.getFireBase().child("conversas");
            fireBase.child(idRemetente).child(idDestinatario).setValue(conversa);

            return true;

        }catch (Exception e){
            e.printStackTrace();

            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        fireBase.removeEventListener(valueMensagem);
    }
}
