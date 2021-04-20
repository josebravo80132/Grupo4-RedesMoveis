package com.example.grupo4_redesmoveis.ui.login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.grupo4_redesmoveis.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterFragment extends Fragment {

    private boolean registerValidation;
    private final String urlUsers = "https://database420-a765.restdb.io/rest/utilizadores";

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        EditText edText_username = view.findViewById(R.id.user_name);
        EditText edText_email = view.findViewById(R.id.user_mail);
        EditText edText_pwd = view.findViewById(R.id.user_pass);
        EditText edText_pwd2 = view.findViewById(R.id.user_pass3);
        EditText edText_age = view.findViewById(R.id.edText_age);
        RadioGroup radioGroup = view.findViewById(R.id.radioGr);
        Button btn_confirmarReg = view.findViewById(R.id.button_register);

        Bundle bundle = this.getArguments();
        if(bundle != null) {

            String nome = bundle.getString("name");
            String email = bundle.getString("username");
            System.out.println(nome+"\n "+email);

            edText_username.setText(nome);
            edText_email.setText(email);
        };



        /** BTN:  Confirmar Registo **/
        btn_confirmarReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username, email, pwd, pwd2, gender;
                int age;
                boolean matchPWD = true;

                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = view.findViewById(selectedId);


                if(selectedId == -1){
                    Toast.makeText(getContext(),"Gender not selected ...", Toast.LENGTH_SHORT).show();
                }


                username = edText_username.getText().toString();
                email = edText_email.getText().toString();
                pwd = edText_pwd.getText().toString();
                pwd2 = edText_pwd2.getText().toString();
                age = Integer.parseInt(String.valueOf(edText_age.getText()));

                if(!pwd.equals(pwd2)){
                    matchPWD = false;
                    Toast.makeText(getContext(),"Incorrect passwords", Toast.LENGTH_SHORT).show();
                }


                if( isUsernameAvailable(username, email) && selectedId != -1 && matchPWD ) {
                    gender = radioButton.getText().toString();
                    try {   saveNewRegister(username, email, pwd, age, gender);     } catch (JSONException e) { e.printStackTrace(); }
                }
            }
        });
        return view;
    }

    public boolean isUsernameAvailable(String usr, String mail){
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
       // String urlUsers = "https://database420-a765.restdb.io/rest/users";
        registerValidation = true;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, urlUsers, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    String usernameAux, emailAux, pwdAux;
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject data = response.getJSONObject(i);
                        usernameAux = data.getString("username");
                        emailAux = data.getString("email");

                        if(mail.equals(emailAux)){ Toast.makeText(getContext(), "Email já registado!", Toast.LENGTH_LONG).show();
                            registerValidation = false;
                        }
                        if(usr.equals(usernameAux)){ Toast.makeText(getContext(), "Username: "+usr+ "já está a ser utilizado!", Toast.LENGTH_LONG).show();
                            registerValidation = false;
                        }

                        if(!registerValidation){
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
        return  registerValidation;
    }
    public void saveNewRegister(String usr, String mail, String pwd, int age, String gender) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));


        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", usr);
        jsonBody.put("password", pwd);
        jsonBody.put("email", mail);
        jsonBody.put("Age", age);
        jsonBody.put("Gender", gender);

        final String requestBody = jsonBody.toString();


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, urlUsers, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work! "+error.networkResponse);
            }
        }){
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody()   {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
            public Map<String,String> getHeaders(){
                Map<String,String> params = new HashMap<String,String>();
                params.put("content-type","application/json");
                params.put("x-apikey", "8d2d611089b681ce18bf0de2362853c55e692");
                params.put("cache-control", "no-cache");
                return params;
            }

        };
        queue.add(jsonArrayRequest);
    }
}