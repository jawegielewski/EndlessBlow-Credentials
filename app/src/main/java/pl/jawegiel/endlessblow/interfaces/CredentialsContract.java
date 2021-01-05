package pl.jawegiel.endlessblow.interfaces;

import java.util.List;

public interface CredentialsContract {

    interface Model {
        interface RestModel {
            void checkLogin(OnFinishedLoginListener onFinishedListener, String login, String passWhenLogin, List<String> usersLoggedOnCredential);
            interface OnFinishedLoginListener {
                void onFinishedLogin(String body);
                void onFailureLogin(Throwable t);
            }
            void checkRegistration(OnFinishedRegisterListener onFinishedListener, String nameWhenRegister, String passWhenRegister, String emailWhenRegister);
            interface OnFinishedRegisterListener {
                void onFinishedRegister(String body);
                void onFailureRegister(Throwable t);
            }
        }
        interface SharedPreferencesModel {
            void saveSomeCredentials(String nameLogin, String passLogin);
            void saveEmptyCredentials();
            String getNameCredential();
            String getPassCredential();
            boolean getCbCredential();
        }
    }


    interface View {
        void showBigProgressDialog();
        void hideBigProgressDialog();
        void setPropertiesWhenLoginIsTrue();
        void setPropertiesWhenLoginIsFalse();
        void setPropertiesWhenRegisterIsTrue();
        void setPropertiesWhenRegisterIsFalse();
        void setRegisterTextViewText(String response);
        void setNameCredential(String name);
        void setPassCredential(String pass);
        void setCbCredential(boolean cbState);
    }


    interface Presenter {
        void checkLoginPresenter(String login, String passWhenLogin, List<String> usersLoggedOnCredential);
        void checkRegistrationPresenter(String nameWhenRegister, String passWhenRegister, String emailWhenRegister);
        void saveCredentials(String nameLogin, String passLogin, boolean saveCredentials);
        void setNameCredential();
        void setPassCredential();
        void setCbCredential();
    }
}
