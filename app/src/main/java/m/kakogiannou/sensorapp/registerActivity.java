package m.kakogiannou.sensorapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class registerActivity extends AppCompatActivity {

        EditText name, email, phone, password;
        Button register;
        TextView login;
        boolean isNameValid, isEmailValid, isPhoneValid, isPasswordValid;
        TextInputLayout nameError, emailError, phoneError, passError;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);

            email = (EditText) findViewById(R.id.email);
            password = (EditText) findViewById(R.id.password);
            login = (TextView) findViewById(R.id.login);
            register = (Button) findViewById(R.id.register);
            nameError = (TextInputLayout) findViewById(R.id.nameError);
            emailError = (TextInputLayout) findViewById(R.id.emailError);
            phoneError = (TextInputLayout) findViewById(R.id.phoneError);
            passError = (TextInputLayout) findViewById(R.id.passError);

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SetValidation();
                    JSONObject jsonBody = new JSONObject();

                    try{
                        jsonBody.put("username", email.getText().toString());
                        jsonBody.put("password", password.getText().toString());
                        final String requestBody = jsonBody.toString();
                        Log.d("JsonObject", requestBody);

                        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        String URL = "http://192.168.2.6:8080/users/register";

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("VOLLEY", response);
                                if (response.equals("200") ) {
                                    Intent i=new Intent(registerActivity.this,MainActivity.class);
                                    startActivity(i);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("VOLLEY", error.toString());
                                Toast.makeText(getApplicationContext(), "User already exists", Toast.LENGTH_LONG).show();
                            }
                        }) {
                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8";
                            }

                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                try {
                                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                                } catch (UnsupportedEncodingException uee) {
                                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                                    return null;
                                }
                            }

                            @Override
                            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                                String responseString = "";
                                if (response != null) {
                                    responseString = String.valueOf(response.statusCode);
                                    // can get more details such as response.headers
                                }
                                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                            }
                        };

                        requestQueue.add(stringRequest);

                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // redirect to LoginActivity
                    Intent intent = new Intent(getApplicationContext(), loginActivity.class);
                    startActivity(intent);
                }
            });
        }

        public void SetValidation() {

            // Check for a valid email address.
            if (email.getText().toString().isEmpty()) {
                emailError.setError(getResources().getString(R.string.email_error));
                isEmailValid = false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                emailError.setError(getResources().getString(R.string.error_invalid_email));
                isEmailValid = false;
            } else  {
                isEmailValid = true;
                emailError.setErrorEnabled(false);
            }

            // Check for a valid password.
            if (password.getText().toString().isEmpty()) {
                passError.setError(getResources().getString(R.string.password_error));
                isPasswordValid = false;
            } else if (password.getText().length() < 6) {
                passError.setError(getResources().getString(R.string.error_invalid_password));
                isPasswordValid = false;
            } else  {
                isPasswordValid = true;
                passError.setErrorEnabled(false);
            }

            if (isEmailValid && isPasswordValid) {
                Toast.makeText(getApplicationContext(), "Successfully", Toast.LENGTH_SHORT).show();
            }

        }
}
