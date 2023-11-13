package pl.jawegiel.endlessblow.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import pl.jawegiel.endlessblow.R;
import pl.jawegiel.endlessblow.interfaces.CredentialsContract;
import pl.jawegiel.endlessblow.model.RestModel;
import pl.jawegiel.endlessblow.model.SharedPreferencesModel;
import pl.jawegiel.endlessblow.presenter.CredentialsPresenter;

public class Credentials extends AppCompatActivity implements CredentialsContract.View {

    private EditText etNameLogin, etPassLogin, etEmailRegistration, etPassRegistration, etPassConfirmationRegistration, etNameRegistration;
    private CheckBox cbSaveCredentials, cbTerms;
    private TextView tvLoginStatus, tvRegistrationStatus;
    private ProgressBar pbBig;
    private Button bOk;
    private CredentialsPresenter credentialsPresenter;
    private List<String> usersLoggedOnCredential = new ArrayList<>();
    private RestModel restModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credentials_layout);
        Volley.newRequestQueue(this);
        initFields();

        restModel = new RestModel(this);
        credentialsPresenter = new CredentialsPresenter(this, restModel, new SharedPreferencesModel((this)));

        credentialsPresenter.setNameCredential();
        credentialsPresenter.setPassCredential();
        credentialsPresenter.setCbCredential();

        findViewById(R.id.tvTerms).setOnClickListener(v -> initRegulationsDialog());

        bOk = findViewById(R.id.bOk);
        bOk.setOnClickListener(v -> {
            credentialsPresenter.saveCredentials(etNameLogin.getText().toString(), etPassLogin.getText().toString(), cbSaveCredentials.isChecked());
            Intent i = new Intent(getApplicationContext(), Gra.class);
            i.putExtra("userLogin", etNameLogin.getText().toString());
            i.putExtra("userPass", etPassLogin.getText().toString());
            i.putExtra("onlineGame", true);
            startActivity(i);
        });

        findViewById(R.id.bLogin).setOnClickListener(v -> {
            if (usersLoggedOnCredential.size() > 0) {
                restModel.logout(usersLoggedOnCredential.get(0));
            }
            credentialsPresenter.checkLoginPresenter(etNameLogin.getText().toString(), etPassLogin.getText().toString(), usersLoggedOnCredential);
        });


        findViewById(R.id.bRegistration).setOnClickListener(v -> {
            if (etPassRegistration.getText().toString().equals(etPassConfirmationRegistration.getText().toString())
                    && !etNameRegistration.getText().toString().equals("")
                    && !etEmailRegistration.getText().toString().equals("") && cbTerms.isChecked()) {
                credentialsPresenter.checkRegistrationPresenter(etNameRegistration.getText().toString(), etPassRegistration.getText().toString(), etEmailRegistration.getText().toString());
            } else
                Toast.makeText(Credentials.this, "Complete the registration fields correctly.", Toast.LENGTH_SHORT).show();
        });
    }

    public void initFields() {
        etNameLogin = findViewById(R.id.etNameLogin);
        etPassLogin = findViewById(R.id.etPassLogin);
        cbSaveCredentials = findViewById(R.id.cbSaveCredentials);
        etNameRegistration = findViewById(R.id.etName);
        etEmailRegistration = findViewById(R.id.etEmailRegistration);
        etPassRegistration = findViewById(R.id.etPassRegistration);
        etPassConfirmationRegistration = findViewById(R.id.etPassConfirmationRegistration);
        cbTerms = findViewById(R.id.cbTerms);
        tvLoginStatus = findViewById(R.id.tvLoginStatus);
        tvRegistrationStatus = findViewById(R.id.tvRegistrationStatus);
        pbBig = findViewById(R.id.bigProgressBar);
    }

    public void initRegulationsDialog() {
        AlertDialog.Builder dialogRegulations = new AlertDialog.Builder(Credentials.this);
        dialogRegulations.setTitle("Terms and conditions");
        dialogRegulations.setMessage("Content");
        dialogRegulations.setCancelable(false);
        dialogRegulations.setNeutralButton("OK", (dialog1, which) -> dialog1.dismiss());
        dialogRegulations.show();
    }

    @Override
    public void showBigProgressDialog() {
        pbBig.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideBigProgressDialog() {
        pbBig.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setPropertiesWhenLoginIsTrue() {
        bOk.setEnabled(true);
        tvLoginStatus.setText(getResources().getString(R.string.login_correct));
        tvLoginStatus.setTextColor(ContextCompat.getColor(this, R.color.green));
    }

    @Override
    public void setPropertiesWhenLoginIsFalse() {
        bOk.setEnabled(false);
        tvLoginStatus.setText(getResources().getString(R.string.login_incorrect));
        tvLoginStatus.setTextColor(ContextCompat.getColor(this, R.color.red));
    }

    @Override
    public void setPropertiesWhenRegisterIsTrue() {
        tvRegistrationStatus.setTextColor(ContextCompat.getColor(this, R.color.green));
    }

    @Override
    public void setPropertiesWhenRegisterIsFalse() {
        tvRegistrationStatus.setTextColor(ContextCompat.getColor(this, R.color.red));
    }

    @Override
    public void setRegisterTextViewText(String response) {
        tvRegistrationStatus.setText(response);
    }

    @Override
    public void setNameCredential(String name) {
        etNameLogin.setText(name);
    }

    @Override
    public void setPassCredential(String pass) {
        etPassLogin.setText(pass);
    }

    @Override
    public void setCbCredential(boolean cbState) {
        cbSaveCredentials.setChecked(cbState);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
