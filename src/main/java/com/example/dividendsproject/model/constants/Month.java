package com.example.dividendsproject.model.constants;

public enum Month { //맵핑 되기 위한 문자열과 그 문자열에 해당하는 숫자 값 두 속성을 가짐

    JAN("Jan",1),
    Feb("Feb",2),
    MAR("Mar",3),
    APR("Apr",4),
    MAY("May",5),
    JUN("Jun",6),
    JUL("Jul",7),
    AUG("Aug",8),
    SEP("Sep",9),
    OCT("Oct",10),
    NDV("Nov",11),
    DEC("Dec",12);

    private String s;
    private int number;

    Month(String s, int n) {
        this.s = s;
        this.number = n;
    }

    //s문자열을 받으면 해당하는 n을 찾아주는 메소드
    public static int strToNumber(String s) {
        //s와 동일한 값을 만나면 숫자값 반환
        for(var m : Month.values()) {
            if(m.s.equals(s)){
                return m.number;
            }
        }

        return -1; //못찾음
    }
}
