package pl.jawegiel.endlessblow.presenter;

import java.util.List;

import pl.jawegiel.endlessblow.interfaces.CredentialsContract;
import pl.jawegiel.endlessblow.model.RestModel;
import pl.jawegiel.endlessblow.model.SharedPreferencesModel;

public class CredentialsPresenter implements CredentialsContract.Presenter, CredentialsContract.Model.RestModel.OnFinishedLoginListener, CredentialsContract.Model.RestModel.OnFinishedRegisterListener{

    CredentialsContract.Model.RestModel restModel;
    CredentialsContract.Model.SharedPreferencesModel sharedPreferencesModel;
    CredentialsContract.View view;

    public CredentialsPresenter(CredentialsContract.View view, RestModel restModel, SharedPreferencesModel sharedPreferencesModel) {
        this.view = view;
        this.restModel = restModel;
        this.sharedPreferencesModel = sharedPreferencesModel;
    }

    @Override
    public void checkLoginPresenter(String login, String passWhenLogin, List<String> usersLoggedOnCredential) {
        view.showBigProgressDialog();
        restModel.checkLogin(this, login, passWhenLogin, usersLoggedOnCredential);
    }

    @Override
    public void checkRegistrationPresenter(String nameWhenRegister, String passWhenRegister, String emailWhenRegister) {
        view.showBigProgressDialog();
        restModel.checkRegistration(this, nameWhenRegister, passWhenRegister, emailWhenRegister);
    }

    @Override
    public void saveCredentials(String nameLogin, String passLogin, boolean saveCrredentials) {
        if(saveCrredentials) sharedPreferencesModel.saveSomeCredentials(nameLogin, passLogin);
        else sharedPreferencesModel.saveEmptyCredentials();
    }

    @Override
    public void setNameCredential() {
        view.setNameCredential(sharedPreferencesModel.getNameCredential());
    }

    @Override
    public void setPassCredential() {
        view.setPassCredential(sharedPreferencesModel.getPassCredential());
    }

    @Override
    public void setCbCredential() {
        view.setCbCredential(sharedPreferencesModel.getCbCredential());
    }


    @Override
    public void onFinishedLogin(String body) {
        if(body.contains("true")) {
            view.setPropertiesWhenLoginIsTrue();
        }
        if(body.contains("false")) {
            view.setPropertiesWhenLoginIsFalse();
        }
        view.hideBigProgressDialog();
    }

    @Override
    public void onFailureLogin(Throwable t) {
        view.hideBigProgressDialog();
    }

    @Override
    public void onFinishedRegister(String body) {
        view.setRegisterTextViewText(body);
        if (body.contains("added successfully"))
            view.setPropertiesWhenRegisterIsTrue();
        else
            view.setPropertiesWhenRegisterIsFalse();
        view.hideBigProgressDialog();

    }

    @Override
    public void onFailureRegister(Throwable t) {
        view.hideBigProgressDialog();
    }
}
