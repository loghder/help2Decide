package com.hebut.help2decide;

import java.util.ArrayList;

public class Voice {

    public ArrayList<WSBean> ws;

    public class WSBean {
        public ArrayList<CWBean> cw;
    }

    public class CWBean {
        public String w;
    }
}