package com.gnomes.common.util;

/**
 * プリミティブ型の出力パラメータを保持する
 * 以下のように使用する。
 *
 *  public static void main(String[] args) {
 *      String x = "foo";
 *      Holder<String> h = new Holder(x);
 *      getString(h);
 *      System.out.println(h.value);
 *  }
 *
 *  public static void getString(Holder<String> output){
 *      output.value = "Hello World"
 *  }
 *
 * <!-- TYPE DESCRIPTION --><pre>
 * </pre>
 */
/* ========================== MODIFICATION HISTORY ==========================
 * Release  Date       ID/Name                   Comment
 * --------------------------------------------------------------------------
 * R0.01.01 2016/08/19 YJP/K.Gotanda              初版
 * [END OF MODIFICATION HISTORY]
 * ==========================================================================
 */
public class Holder<T> {
    public Holder(T value) {
        this.value = value;
    }
    public T value;

    public String toString() {
        return value.toString();
    }
}
