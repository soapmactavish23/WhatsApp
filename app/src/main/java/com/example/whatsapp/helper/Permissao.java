package com.example.whatsapp.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {

    public static boolean validaPermissao(int requestCode,Activity activity, String[] permissoes){

        if(Build.VERSION.SDK_INT >= 23){

            List<String> listaPermissoes = new ArrayList<String>();

            for (String permissao : permissoes){

                Boolean validaPerminssao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                if(!validaPerminssao) listaPermissoes.add(permissao);

            }

            //Caso a lista esteja vazia
            if(listaPermissoes.isEmpty()){
                return true;
            }

            String[] novasPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novasPermissoes);
            ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode);

        }

        return true;

    }

}
