package pl.jawegiel.endlessblow.model;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.jawegiel.endlessblow.dto.LvlAndExpDto;
import pl.jawegiel.endlessblow.dto.UsersOnlineDto;
import pl.jawegiel.endlessblow.interfaces.CredentialsContract;
import pl.jawegiel.endlessblow.interfaces.OnHpAktOnlineSuccess;
import pl.jawegiel.endlessblow.interfaces.OnSearchUsers;
import pl.jawegiel.endlessblow.other.ChatAdapter;
import pl.jawegiel.endlessblow.other.GameSurface;
import pl.jawegiel.endlessblow.adapters.RightDrawerAdapter;
import pl.jawegiel.endlessblow.other.RightDrawerItem;
import pl.jawegiel.endlessblow.adapters.SearchAdapter;
import pl.jawegiel.endlessblow.utility.Api;
import pl.jawegiel.endlessblow.utility.DBHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestModel implements CredentialsContract.Model.RestModel {

    private final Context context;
    private int level, exp, pointsUnass, hpAkt, mpAkt, hpMax, mpMax;
    List<ChatMsg> oldMsgs = new ArrayList<>();
    List<User> searchUsers = new ArrayList<>();
    List<UsersOnlineDto> usersOnline = new ArrayList<>();
    LvlAndExpDto oldLvlAndExpDto = new LvlAndExpDto(0, 0), newLvlAndExpDto;
    DBHelper dbHelper;

    public RestModel(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
        dbHelper.getReadableDatabase();
    }



    public void checkLogin(String login, String passWhenLogin) {
        Call<String> result = Api.getClient().checkLogin(login, passWhenLogin);
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("checkLogin", t.toString());
                call.cancel();
                call.clone().enqueue(this);
            }
        });
    }

    @Override
    public void checkLogin(final OnFinishedLoginListener onFinishedListener, String login, String passWhenLogin, List<String> usersLoggedOnCredential) {
        Call<String> result = Api.getClient().checkLogin(login, passWhenLogin);
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                onFinishedListener.onFinishedLogin(response.body());
                usersLoggedOnCredential.add(login);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("checkLogin", t.toString());
                onFinishedListener.onFailureLogin(t);
            }
        });
    }

    @Override
    public void checkRegistration(final OnFinishedRegisterListener onFinishedListener, String nameWhenRegister, String passWhenRegister, String emailWhenRegister) {
        Call<String> result = Api.getClient().addUser(nameWhenRegister, passWhenRegister, emailWhenRegister);
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body() != null)
                    onFinishedListener.onFinishedRegister(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("checkRegistration", t.toString());
                onFinishedListener.onFailureRegister(t);
            }
        });
    }

    public void getMaxNumberOfUsersOnline(TextView textView) {
        Call<String> result = Api.getClient().getMaxUsersOnline();
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                textView.setText(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("getMaxNumberOfUsersOnli", t.toString());
                call.cancel();
                call.clone().enqueue(this);
            }
        });
    }

    public void setLevel(String name) {
        Call<String> result = Api.getClient().setNewLevel(name);
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(context, response.body(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("setLevel", t.toString());
                call.cancel();
                call.clone().enqueue(this);
            }
        });
    }

    public void setExp(String name, int exp, TextView textView) {
        Call<String> result = Api.getClient().updateExp(name, exp);
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                textView.setText(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("setExp", t.toString());
                call.cancel();
                call.clone().enqueue(this);
            }
        });
    }

    public int getLevel(String name, TextView tvLevel) {
        Call<String> result = Api.getClient().getLevel(name);
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                tvLevel.setText(response.body());
                if (response.body() != null) level = Integer.parseInt(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("getLevel", t.toString());
                call.cancel();
            }
        });
        return level;
    }















    public LvlAndExpDto getLvlAndExp(String name, TextView tvLvl, TextView tvExp, TextView tvExpRequired) {
        Call<LvlAndExpDto> result = Api.getClient().getLvlAndExp(name);
        result.enqueue(new Callback<LvlAndExpDto>() {
            @Override
            public void onResponse(Call<LvlAndExpDto> call, Response<LvlAndExpDto> response) {

                if(response.body()!=null) {
                    newLvlAndExpDto = response.body();
                    tvLvl.setText(String.valueOf(newLvlAndExpDto.getLevel()));
                    tvExp.setText(String.valueOf(newLvlAndExpDto.getExp()));
                    if((oldLvlAndExpDto.getLevel()!=newLvlAndExpDto.getLevel() || oldLvlAndExpDto.getExp()!=newLvlAndExpDto.getExp())) {
                        int j = -newLvlAndExpDto.getExp();
                        int k = 0;

                        for (int i = 0; i <= newLvlAndExpDto.getLevel() + 1; i++)
                            k = k + dbHelper.getExpRequirementsByLvl(i);
                        int l = j + k;
                        tvExpRequired.setText(String.valueOf(l));
                    }
                    oldLvlAndExpDto = newLvlAndExpDto;
                }
            }


            @Override
            public void onFailure(Call<LvlAndExpDto> call, Throwable t) {
                Log.e("getLvlAndExp", t.toString());
                call.cancel();
            }
        });
        return newLvlAndExpDto;
    }


















    public int getExp(String name, TextView textView) {
        Call<String> result = Api.getClient().getExp(name);
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null) {
                    textView.setText(response.body());
                    exp = Integer.parseInt(response.body());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("getExp", t.toString());
                call.cancel();
            }
        });
        return exp;
    }

    public void setHpStat(String name) {
        Call<String> result = Api.getClient().setHpStat(name);
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(context, "failure " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.e("tag", t.toString());
            }
        });
    }

    public int getPointsUnass(String name, TextView textView) {
        Call<String> result = Api.getClient().getPointsUnass(name);
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body() != null) pointsUnass = Integer.parseInt(response.body());
                textView.setText(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(context, "failure " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.e("tag", t.toString());
            }
        });
        return pointsUnass;
    }

    public void setNewPointsUnass(String name, TextView textView) {
        Call<String> result = Api.getClient().setNewPointsUnass(name);
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                getPointsUnass(name, textView);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("tag", t.toString());
            }
        });
    }

    public int getHpAkt(String name, TextView textView, ChibiCharacter cc, OnHpAktOnlineSuccess onHpAktOnlineSuccess) {

        Call<String> result = Api.getClient().getHpAkt(name);
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null) {
                    hpAkt = Integer.parseInt(response.body());
                    textView.setText(response.body());
                    cc.setHp(Integer.parseInt(response.body()));
                    onHpAktOnlineSuccess.onHpAktOnlineSuccess(Integer.parseInt(response.body()));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("getHpAkt", t.toString());
                call.cancel();
                call.clone().enqueue(this);
            }
        });
        return hpAkt;
    }

    public int getHpMax(String name, TextView textView, ChibiCharacter cc) {

        Call<String> result = Api.getClient().getHpMax(name);
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null) {
                    hpMax = Integer.parseInt(response.body());
                    textView.setText(response.body());
                    cc.setHp(Integer.parseInt(response.body()));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("getHpMax", t.toString());
                call.cancel();
                call.clone().enqueue(this);
            }
        });
        return hpMax;
    }

    public int getMpMax(String name, TextView textView, ChibiCharacter cc) {

        Call<String> result = Api.getClient().getMpMax(name);
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null) {
                    mpMax = Integer.parseInt(response.body());
                    textView.setText(response.body());
                    cc.setHp(Integer.parseInt(response.body()));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("getMpMax", t.toString());
                call.cancel();
                call.clone().enqueue(this);
            }
        });
        return mpMax;
    }

    public void setHpAkt(String name, int hp) {
        Call<String> result = Api.getClient().setHpAkt(name, hp);
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("setHpAkt", t.toString());
                call.cancel();
                call.clone().enqueue(this);
            }
        });
    }

    public int getMpAkt(String name, TextView textView, ChibiCharacter cc) {

        Call<String> result = Api.getClient().getMpAkt(name);
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null) {
                    mpAkt = Integer.parseInt(response.body());
                    textView.setText(response.body());
                    cc.setHp(Integer.parseInt(response.body()));
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("getMpAkt", t.toString());
                call.cancel();
                call.clone().enqueue(this);
            }
        });
        return mpAkt;
    }

    public void addChatMsg(String name, String msg) {
        Call<String> result = Api.getClient().addChatMsg(name, msg);
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("addChatMessage", t.getMessage());
                call.cancel();
                call.clone().enqueue(this);
            }
        });
    }


















    public List<ChatMsg> getChatMsgs(ChatAdapter.ItemClickListener itemClickListener, RecyclerView recyclerView) {
        List<ChatMsg> newMsgs = new ArrayList<>();

        Call<List<ChatMsg>> result = Api.getClient().getChatMsgs();
        result.enqueue(new Callback<List<ChatMsg>>() {
            @Override
            public void onResponse(Call<List<ChatMsg>> call, Response<List<ChatMsg>> response) {
                newMsgs.addAll(response.body());
                if (!oldMsgs.equals(newMsgs)) {
                    ChatAdapter adapter3 = new ChatAdapter(context, newMsgs);
                    adapter3.setClickListener(itemClickListener);
                    recyclerView.scrollToPosition(adapter3.getItemCount() - 1);
                    recyclerView.setAdapter(adapter3);
                }
                oldMsgs = new ArrayList<>(newMsgs);
            }

            @Override
            public void onFailure(Call<List<ChatMsg>> call, Throwable t) {
                Log.e("getChatMsgs", t.getMessage());
                call.cancel();
                result.cancel();
            }
        });
        return newMsgs;
    }

    public List<User> getUsers(RecyclerView recyclerView, OnSearchUsers onSearchUsers, List<RightDrawerItem> rightItems, RightDrawerAdapter rightAdapter, String login) {
        onSearchUsers.onSearchUsersStarted();
        searchUsers.clear();
        Call<List<User>> result = Api.getClient().getUsersNames();
        result.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.body() != null) {

                    searchUsers.addAll(response.body());
                    SearchAdapter searchAdapter = new SearchAdapter(context, searchUsers, rightItems, rightAdapter, login);
                    recyclerView.setAdapter(searchAdapter);
                    onSearchUsers.onSearchUsersFinished();
                }
                else
                    Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("getUsers", t.toString());
                call.cancel();
                call.clone().enqueue(this);
            }
        });
        return searchUsers;
    }

    public List<User> getSpecificUsers(RecyclerView recyclerView, String pattern, OnSearchUsers onSearchUsers, List<RightDrawerItem> rightItems, RightDrawerAdapter rightAdapter, String login) {
        onSearchUsers.onSearchUsersStarted();
        searchUsers.clear();
        Call<List<User>> result = Api.getClient().getSpecificUsersNames(pattern);
        result.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.body() != null) {

                    searchUsers.addAll(response.body());
                    SearchAdapter searchAdapter = new SearchAdapter(context, searchUsers, rightItems, rightAdapter, login);
                    recyclerView.setAdapter(searchAdapter);
                    onSearchUsers.onSearchUsersFinished();
                }
                else
                    Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.e("getSpecificUsers", t.toString());
                call.cancel();
                call.clone().enqueue(this);
            }
        });
        return searchUsers;
    }
















    public void logout(String name) {
        Call<String> result = Api.getClient().logout(name);
        result.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
               // Toast.makeText(context, "You logged out "+response.body(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("logout", t.toString());
                Toast.makeText(context, "logout "+t.getMessage(), Toast.LENGTH_SHORT).show();
                call.cancel();
                call.clone().enqueue(this);
            }
        });
    }



    public List<UsersOnlineDto> getUsersOnlinePositions(GameSurface gs) {
        Call<List<UsersOnlineDto>> result = Api.getClient().getUsersOnlinePositions();
        result.enqueue(new Callback<List<UsersOnlineDto>>() {
            @Override
            public void onResponse(Call<List<UsersOnlineDto>> call, Response<List<UsersOnlineDto>> response) {
                if(response.body() != null) {
                    gs.players.clear();
                    usersOnline.clear();
                    usersOnline.addAll(response.body());

                    for(int i=0; i<usersOnline.size(); i++)
                        gs.addOtherPlayer(usersOnline.get(i).getId(), usersOnline.get(i).getX(), usersOnline.get(i).getY());

                    //gs.updatePlayersLocationAndId(usersOnline, Util.centerPositions(usersOnline, gs));


                }
                else
                    Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Call<List<UsersOnlineDto>> call, Throwable t) {
                Log.e("getUsersOnlinePositions", t.toString());
                call.cancel();
            }
        });
        return usersOnline;
    }
}