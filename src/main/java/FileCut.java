import redis.clients.jedis.Jedis;

import java.io.*;

public class FileCut {
    public static void main(String[] args) {
        try {
            Jedis jedis=new Jedis("127.0.0.1");
            String encoding="GBK";
//            File bigfile=new File("C:\\Users\\001302\\Desktop\\001\\rest2.txt");
            File bigfile=new File("C:\\Users\\001302\\Desktop\\001\\深圳.txt");
            if(bigfile.isFile() && bigfile.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(bigfile),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                int i=0;
                StringBuffer sb=new StringBuffer();
                int j=0;
                while((lineTxt = bufferedReader.readLine()) != null){
//                    jedis.sadd("hz_all",lineTxt);
                    i++;
                    if(jedis.sismember("hz_all",lineTxt)){
                        j++;
                        sb.append(lineTxt).append("\r\n");
                        System.out.println(lineTxt);
                        continue;
                    }
//                    sb.append(lineTxt).append("\r\n");
//                    if(i%4000==0){
//                        j++;
//                        File destFile = new File("C:\\Users\\001302\\Desktop\\001\\5\\"+j+".txt");
//                        if(!destFile.getParentFile().exists()){
//                            destFile.mkdirs();
//                        }
//                        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(destFile), "GBK");
//                        BufferedWriter bw = new BufferedWriter(osw);
//                        bw.write(sb.toString());
//                        bw.close();
//                        osw.close();
//                        sb.setLength(0);
//                    }
                }
//                j++;
//                File destFile = new File("C:\\Users\\001302\\Desktop\\001\\5\\"+j+".txt");
                File destFile = new File("C:\\Users\\001302\\Desktop\\001\\exist.txt");
                OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(destFile), "GBK");
                BufferedWriter bw = new BufferedWriter(osw);
                bw.write(sb.toString());
                bw.close();
                osw.close();
                read.close();
                System.out.println(i);
                System.out.println(j);
                jedis.close();
            }else{
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
    }
}
