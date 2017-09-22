package com.example.mingxuan.myapplication;


import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2017/8/31 0031.
 */

public class TestView {
    private int heigt;      //读入文本的行数
    private String []context = new String[1024];    //存储读入的文本
    /*
     *@parameter wordNum  设置每行显示的字数
     * 构造函数将文本读入，将每行字符串切割成小于等于35个字符的字符串  存入字符数组
     *
     */
    public TestView(int wordNum ,InputStreamReader in) throws Exception {
        int i=0;
        BufferedReader br = new BufferedReader(in);
        String s;
        while((s=br.readLine())!=null){
            //读入时去掉  空白的行
//          if(s.trim().equals("")){
//              continue;
//          }
            if(s.length()>wordNum){
                int k=0;
                while(k+wordNum<=s.length()){
                    context[i++] = s.substring(k, k+wordNum);
                    k=k+wordNum;
                }
                context[i++] = s.substring(k,s.length());
            }
            else{
                context[i++]=s;
            }
        }
        this.heigt = i;
        in.close();
        br.close();
    }


    public int getHeigt() {
        return heigt;
    }

    public String[] getContext() {
        return context;
    }
}
