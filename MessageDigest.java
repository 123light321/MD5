import java.util.ArrayList;

/**
 * Created by shahrose on 10/30/2019.
 */
public class MessageDigest {

    String originalMessage;
    ArrayList<byte[]> messageBlocks2 = new ArrayList<>();

    //K Table
    int[] kTable = {
            0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee,
            0xf57c0faf, 0x4787c62a, 0xa8304613, 0xfd469501,
            0x698098d8, 0x8b44f7af, 0xffff5bb1, 0x895cd7be,
            0x6b901122, 0xfd987193, 0xa679438e, 0x49b40821,
            0xf61e2562, 0xc040b340, 0x265e5a51, 0xe9b6c7aa,
            0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8,
            0x21e1cde6, 0xc33707d6, 0xf4d50d87, 0x455a14ed,
            0xa9e3e905, 0xfcefa3f8, 0x676f02d9, 0x8d2a4c8a,
            0xfffa3942, 0x8771f681, 0x6d9d6122, 0xfde5380c,
            0xa4beea44, 0x4bdecfa9, 0xf6bb4b60, 0xbebfbc70,
            0x289b7ec6, 0xeaa127fa, 0xd4ef3085, 0x04881d05,
            0xd9d4d039, 0xe6db99e5, 0x1fa27cf8, 0xc4ac5665,
            0xf4292244, 0x432aff97, 0xab9423a7, 0xfc93a039,
            0x655b59c3, 0x8f0ccc92, 0xffeff47d, 0x85845dd1,
            0x6fa87e4f, 0xfe2ce6e0, 0xa3014314, 0x4e0811a1,
            0xf7537e82, 0xbd3af235, 0x2ad7d2bb, 0xeb86d391
    };

    //Buffers
    int a = 0x67452301;
    int b = 0xEFCDAB89;
    int c = 0x98BADCFE;
    int d = 0x10325476;

    //S values
    int[] s = {
            7, 12, 17, 22,
            5, 9, 14, 20,
            4, 11, 16, 23,
            6, 10, 15, 21
    };

    public static void main(String[] args) {

        MessageDigest m = new MessageDigest();
        String input = "abcdefghijklmnopqrstuvwxyzeverrvesarreasabverbrfscbrbrtbrtfvaxwr";

        m.byteConverter(input);
        m.createBlocks();
        String result = toHexString(m.encryptor());

        System.out.println();
        System.out.println("-----------------------------------------------------------");
        System.out.println("                    Result MD5");
        System.out.println("Input Message: "+input);
        System.out.println("Output: " + "0x" + result);
    }

    byte[] encryptor() {
        ArrayList<String> brokenBlocks = new ArrayList<>();
        for (int i = 0; i < messageBlocks2.size(); i++) {
            StringBuilder token = new StringBuilder();
            token.append("");

            System.out.println("-----------------------------------------------------------");
            System.out.println("[32 bit Blocks for Message Block "+i+"]");
            for (int j = 0; j < 64; j=j+4) {
                brokenBlocks.add(Integer.toHexString(Math.abs(messageBlocks2.get(i)[j+3]))+""+Integer.toHexString(Math.abs(messageBlocks2.get(i)[j+2]))
                        +""+Integer.toHexString(Math.abs(messageBlocks2.get(i)[j+1]))+""+Integer.toHexString(Math.abs(messageBlocks2.get(i)[j]))+"");

                System.out.println("["+j/4+"]: "+Integer.toHexString(Math.abs(messageBlocks2.get(i)[j+3]))+""+Integer.toHexString(Math.abs(messageBlocks2.get(i)[j+2]))
                        +""+Integer.toHexString(Math.abs(messageBlocks2.get(i)[j+1]))+""+Integer.toHexString(Math.abs(messageBlocks2.get(i)[j]))+"");
            }
            int A = a;
            int B = b;
            int C = c;
            int D = d;

            System.out.println("-----------------------------------------------------------");
            System.out.println("[Values in Registers for Message Block "+i+"]");
            System.out.println("Step    A           B           C           D           ");
            for (int k = 0; k < 64; k++) {
                int F = 0;
                int div16 = k >>> 4;
                int bufferIndex = k;

                if (0 <= k && k <= 15) {
                    F = F(B, C, D);
                } else if (16 <= k && k <= 31) {
                    F = G(B, C, D);
                    bufferIndex = (k * 5 + 1) & 0x0F;
                } else if (32 <= k && k <= 47) {
                    F = H(B, C, D);
                    bufferIndex = (k * 3 + 5) & 0x0F;
                } else if (48 <= k && k <= 63) {
                    F = I(B, C, D);
                    bufferIndex = (k * 7) & 0x0F;
                }

                int temp = B + Integer.rotateLeft(A + F + Integer.parseUnsignedInt(brokenBlocks.get(bufferIndex), 16) + kTable[k], s[(div16 << 2) | (k & 3)]);

                A = D;
                D = C;
                C = B;
                B = temp;

                System.out.println("[i="+k+"] "+Integer.toUnsignedString(A)+"  "+Integer.toUnsignedString(B)+"   "+Integer.toUnsignedString(C)+"   "+Integer.toUnsignedString(D));
            }

            a = a + A;
            b = b + B;
            c = c + C;
            d = d + D;

            brokenBlocks.clear();
            System.out.println();
        }

        //For making low order byte first
        byte[] md5 = new byte[16];
        int count = 0;
        for (int i = 0; i < 4; i++) {
            int n = (i == 0) ? a : ((i == 1) ? b : ((i == 2) ? c : d));
            for (int j = 0; j < 4; j++) {
                md5[count++] = (byte) n;
                n >>>= 8;
            }
        }
        return md5;
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            sb.append(String.format("%02X", b[i] & 0xFF));
        }
        return sb.toString();
    }

    ArrayList<Byte> in = new ArrayList<>();

    void byteConverter(String input){
        byte[] temp = input.getBytes();
        for (byte b:temp){
            in.add(b);
        }
    }

    void createBlocks() {
        int size = in.size();
            for (int i = 0; ; i++){
                if(size*8 >=448 &&size*8 <= 512 ){
                    if(!(size*8==512)){
                    messageBlocks2.add(padding(64,i*64,in.size()-(i*64),true));
                    messageBlocks2.add(zeroBlock(true));
                    }else {
                        messageBlocks2.add(toByteArrayLimited(i*0,(i+1)*64));
                        messageBlocks2.add(zeroBlock(false));
                    }
                    break;
                }
                else if(size*8 > 512){
                    messageBlocks2.add(toByteArrayLimited(i*0,(i+1)*64));
                    size = size-64;
                }
                else if (size*8 < 448) {
                    messageBlocks2.add(padding(64,i*64,in.size()-(i*64),false));
                    break;
                }
                System.out.println(size);
            }
    }

    byte[] toByteArray(ArrayList<Byte> a){
        byte[] result = new byte[a.size()];
        for(int i = 0; i < a.size(); i++) {
            result[i] = a.get(i);
        }
        return result;
    }

    byte[] toByteArrayLimited(int start,int end){
        byte[] result = new byte[64];
        for(int i = start; i < end; i++) {
            result[i] = in.get(i);
        }
        return result;
    }

    byte[] padding(int size,int start,int remain, boolean zero) {
        ArrayList<Byte> temp = new ArrayList<>(size);

        for (int i=0;i<remain;i++){
            temp.add((in.get(start)));
            start++;
        }
        temp.add((byte)128);

        for (int i = 0; i <64 - (remain+1); i++) {
            temp.add((byte)0);
        }

        if(zero==false){
            temp = lengthAdder(temp);
        }
        return toByteArray(temp);
    }

    byte[] zeroBlock(boolean mode){
        ArrayList<Byte> temp = new ArrayList<>();
        if (mode){
            for (int i = 0; i <64 ; i++) {
                temp.add((byte)0);
            }
        }else {
            temp.add((byte)128);
            for (int i = 0; i <63 ; i++) {
                temp.add((byte)0);
            }
        }

        temp = lengthAdder(temp);
        return toByteArray(temp);
    }

    ArrayList<Byte> lengthAdder(ArrayList<Byte> inCompleteBlock) {

        System.out.println();
        String lenString = String.format("%08X", in.size()*8).replace(' ', '0');
        byte[] lenBytes= lenString.getBytes();

        for (int i=0,pointer=63;i<8;i++,pointer--){
            inCompleteBlock.set(pointer,(byte)Integer.parseInt(((char)lenBytes[i])+"",16));
            if(pointer==60){
                pointer=60;
            }
        }

        return inCompleteBlock;
    }

    int F(int x, int y, int z) {
        return (x & y) | (~x & z);
    }

    int G(int x, int y, int z) {
        return (z & x) | (~z & y);
    }

    int H(int x, int y, int z) {
        return x ^ y ^ z;
    }

    int I(int x, int y, int z) {
        return y ^ (x | ~z);
    }

}
