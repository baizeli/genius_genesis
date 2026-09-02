package miku.united_as_one.genesis.combat.protectedhealth;

import java.nio.charset.StandardCharsets;

public final class ProtectedHealthCipher {
    private static final int SEED = 0x5A3C7E9F;
    private static final byte[] TABLE = table();
    private ProtectedHealthCipher() {}
    public static String encrypt(String plain) {
        if (plain == null) return "";
        byte[] in = plain.getBytes(StandardCharsets.UTF_8), out = new byte[in.length + 2];
        int a=(in.length*7+0xA5)&255,b=(in.length*13+0x3C)&255,s=((a<<8)|b)^SEED;
        out[0]=(byte)(a^0x5A); out[1]=(byte)(b^0x3C);
        for(int i=0;i<in.length;i++){int k=TABLE[(s+i*31)&255]&255,m=((s>>>(i%24))^(s<<((i*3)%16)))&255;out[i+2]=(byte)((in[i]^k)+m);s=Integer.rotateLeft(s,3)^(in[i]&255);}
        return hex(out);
    }
    public static String decrypt(String cipher) {
        if(cipher==null||cipher.isEmpty()) return "";
        byte[] in; try{in=unhex(cipher);}catch(RuntimeException e){return "";}
        if(in.length<2)return ""; int n=in.length-2,a=(in[0]&255)^0x5A,b=(in[1]&255)^0x3C;
        if(((n*7+0xA5)&255)!=a||((n*13+0x3C)&255)!=b)return "";
        byte[] out=new byte[n]; int s=((a<<8)|b)^SEED;
        for(int i=0;i<n;i++){int k=TABLE[(s+i*31)&255]&255,m=((s>>>(i%24))^(s<<((i*3)%16)))&255;out[i]=(byte)((((in[i+2]&255)-m)&255)^k);s=Integer.rotateLeft(s,3)^(out[i]&255);}
        return new String(out,StandardCharsets.UTF_8);
    }
    private static byte[] table(){byte[] t=new byte[256];for(int i=0;i<256;i++)t[i]=(byte)i;int j=0;for(int i=0;i<256;i++){j=(j+(t[i]&255)+((SEED>>>(i%32))&255)+0x37)&255;byte x=t[i];t[i]=t[j];t[j]=x;}return t;}
    private static String hex(byte[] b){char[] h="0123456789ABCDEF".toCharArray(),o=new char[b.length*2];for(int i=0;i<b.length;i++){int v=b[i]&255;o[i*2]=h[v>>>4];o[i*2+1]=h[v&15];}return new String(o);}
    private static byte[] unhex(String s){if((s.length()&1)!=0)throw new IllegalArgumentException();byte[] b=new byte[s.length()/2];for(int i=0;i<s.length();i+=2){int a=Character.digit(s.charAt(i),16),c=Character.digit(s.charAt(i+1),16);if(a<0||c<0)throw new IllegalArgumentException();b[i/2]=(byte)((a<<4)+c);}return b;}
}
