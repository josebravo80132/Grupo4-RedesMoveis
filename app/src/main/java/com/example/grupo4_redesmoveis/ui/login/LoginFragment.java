package com.example.grupo4_redesmoveis.ui.login;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.grupo4_redesmoveis.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class LoginFragment extends Fragment {

    private EditText edText_username, edText_password;
    private CallbackManager callbackManager;
    private ViewFlipper viewFlipper;
    private boolean loginValidation;
    private String fb_name, fb_mail;

    public LoginFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

      /*  int[] images = {R.drawable.running1, R.drawable.fitness1, R.drawable.running2};
        viewFlipper = view.findViewById(R.id.flipimg);
        for(int image: images){
            flipperImages(image);
        }
        */


        LoginButton btn_facebook = view.findViewById(R.id.login_button);
        btn_facebook.setReadPermissions(Arrays.asList("email", "public_profile"));
        btn_facebook.setFragment(this);
        Button btn_registerMail = view.findViewById(R.id.registarComEmail_btn);
        Button btn_login = view.findViewById(R.id.iniciarSessao_btn);



        /** CONTINUE W/ FACEBOOK **/
        callbackManager = CallbackManager.Factory.create();
        // Registering CallbackManager with the LoginButton
        btn_facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Retrieving access token using the LoginResult
                ;
                AccessToken accessToken = loginResult.getAccessToken();
                getFacebookInfo(accessToken);
                if(loginValidation){
                    /** Atividade seguinte -- **/


                }else{  /** 1º login pelo facebook **/
                    Bundle bundle = new Bundle();
                    bundle.putString("name",fb_name);
                    bundle.putString("email",fb_mail);

                    System.out.println(fb_name+"\n "+fb_mail);


                    /*Fragment fragment=new RegisterFragment();
                    fragment.setArguments(bundle);
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.frameContainerS, fragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();*/
                }


            }
            @Override
            public void onCancel() {
            }
            @Override
            public void onError(FacebookException error) {
            }
        });
        /****/

        /** Registar W/ mail **/
        btn_registerMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment=new RegisterFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frameContainerS, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
            }
        });
        /****/

        /** Iniciar Sessão **/
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edText_username = view.findViewById(R.id.edText_username);
                edText_password = view.findViewById(R.id.edText_password);
                String usr = edText_username.getText().toString();
                String pwd = edText_password.getText().toString();

                if( !getLoginResult(usr, pwd, 0) ){
                    //Toast.makeText(getContext(), "Dados inválidos!", Toast.LENGTH_LONG).show();
                    edText_username.setHint("Username or Email");
                    edText_password.setText("");
                }else{
                    /** ATIVIDADE SEGUINTE **/
                }

            }
        });
        /****/





        return  view;
    }



    /** Facebook methods **/
    public void onActivityResult(int requestCode, int resulrCode, Intent data) {//call callbackManager.onActivityResult to pass the login results to the FacebookSDK  via callbackManager.
        callbackManager.onActivityResult(requestCode, resulrCode, data);
        super.onActivityResult(requestCode, resulrCode, data);
    }
    private void getFacebookInfo(AccessToken accessToken) {

        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            //OnCompleted is invoked once the GraphRequest is successful
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {

                    fb_name= object.getString("name");
                    fb_mail = object.getString("email");
                    // String image = object.getJSONObject("picture").getJSONObject("data").getString("url");
                    getLoginResult(fb_mail, null, 1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        // We set parameters to the GraphRequest using a Bundle.
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,picture.width(200)");
        request.setParameters(parameters);
        // Initiate the GraphRequest
        request.executeAsync();
    }
    /****/

    /** VALIDATE LOGIN **/
    public boolean getLoginResult(String username, String password, int method){    // 0 - loginButton , 1 - facebook ....
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        String urlUsers = "https://database420-a765.restdb.io/rest/utilizadores";
        loginValidation = false;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlUsers, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    String usernameAux, emailAux, pwdAux;
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject data = response.getJSONObject(i);
                        usernameAux = data.getString("username");   emailAux = data.getString("email");  pwdAux = data.getString("password");

                        if( method == 0 && ( (username.equals(usernameAux) || username.equals(emailAux)) && password.equals(pwdAux) ) ){
                            loginValidation = true;
                            Toast.makeText(getContext(), "Bem vindo "+usernameAux+"!", Toast.LENGTH_LONG).show();
                            break;
                        }
                        else if( method == 1 && username.equals(emailAux)){     /**  FacebookUser registado **/
                            loginValidation = true;
                            Toast.makeText(getContext(), "Bem vindo "+usernameAux+"!", Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!");
            }
        }) {
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-apikey", "8d2d611089b681ce18bf0de2362853c55e692");
                params.put("cache-control", "no-cache");
                return params;
            }
        };
        queue.add(jsonArrayRequest);
        return loginValidation;
    }
}