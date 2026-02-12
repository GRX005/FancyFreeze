package _1ms.FF;

public enum Perm {
    Freeze("FF.freeze"),
    FreezeAll("FF.freezeall"),
    Bypass("FF.bypass"),
    Reload("FF.reload"),
    Notify("FF.notify");

    public final String s;

    Perm(String perm) {
        this.s = perm;
    }

}
