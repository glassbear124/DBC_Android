package com.tevreden.dbc;

public class C {
    public static int LikeDog = 0;
    public static int LikeCat = LikeDog + 1;

    public static int LangEng = 0;
    public static int LangNed = LangEng + 1;
    public static int LangPap = LangNed + 1;

    public static String baseUrl = "http://cupid.dierenbeschermingcuracao.com/";

    public static final int REQ_CAMERA_CLICK = 101;
    public static final int REQ_READ_EXTERNAL_STORAGE = 102;
    public static final int REQ_WRITE_EXTERNAL_STORAGE = 103;
    public static final int READ_PHONE_STATE = 104;
    public static final int REQ_ACCESS_FINE_LOCATION = 105;
    public static final int REQ_READ_CALENDAR = 106;
    public static final int REQ_WRITE_CALENDAR = 107;
    public static final int REQ_CALL_PHONE = 108;
}


/*

Report Details:

On all the android mobiles there were some errors.
1. Any one without entering his username and password can login
2. When a user edit “My account” information, it automatically changes all other’s users account information too.
3. The app keeps no record of user login, Each time when app is opened the user has to login from start using his email and password, The app must sync up so he remember users id and password and doesn’t ask to login each time.
4. Login with facebook crashes some time, and user can’t login with facebook.
5. Adding pets to favorites sometimes stops working and hangs down. For first few pets it shows it adds in favorites, but for next pets it doesn’t respond
6. Much of the times the pets we add to favorites doesn’t show up in “My favorites”, which needs to be solved.
7. Every-time when we click organizations, and then we select any organization, the information for the organizations shows up. But when we select the mobile number of that organization the app crashes and shows an non-responding prompt on the screen


0.45 signup process fails
1.47 if feed empty, wrong msg "you didnt select any animals as favorites yet" (with dogs and also cats)
2.25 crash
2.56 why need to login again...app should remember user
3.23 facebook login fail


1.04 Login screen (with cat) needs name PET CUPID above new logo
0.15 Login screen (with cat) needs name PET CUPID above new logo

* */