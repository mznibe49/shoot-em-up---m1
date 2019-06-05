package sample.Others;

public class Chrono  {

    public static String timeToHMS(int tempsS) {

        int h =  (tempsS / 3600);
        int m =  ((tempsS % 3600) / 60);
        int s =  (tempsS % 60);

        String r="";

        if(h>0) {r+=h+" h ";}
        if(m>0) {r+=m+" min ";}
        if(s>0) {r+=s+" s";}
        if(h<=0 && m<=0 && s<=0) {r="0 s";}

        return r;
    }


}