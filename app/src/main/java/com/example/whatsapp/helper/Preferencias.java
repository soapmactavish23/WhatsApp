package com.example.whatsapp.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class Preferencias {

    private Context context;
    private SharedPreferences preferences;
    private String NOME_ARQUIVO = "whatsapp.preferencias";
    private int MODE = 0;
    private SharedPreferences.Editor editor;

    private String CHAVE_IDENTIFICADOR = "identificadorUsuarioLogado";
    private String CHAVE_NOME = "nomeUsuarioLogado";

    public Preferencias(Context contextoPametros){

        context = contextoPametros;
        preferences = context.getSharedPreferences(NOME_ARQUIVO, MODE);
        editor = preferences.edit();

    }

    public void salvar(String identificadorUsuarioLogado, String nomeUsuario){
        editor.putString(CHAVE_IDENTIFICADOR, identificadorUsuarioLogado);
        editor.putString(CHAVE_NOME, nomeUsuario);
        editor.commit();
    }

    public String getIdentificador(){
        return  preferences.getString(CHAVE_IDENTIFICADOR, "");
    }

    public String getNOME(){
        return  preferences.getString(CHAVE_NOME, "");
    }
}
