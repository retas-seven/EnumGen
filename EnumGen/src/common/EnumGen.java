package common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnumGen {
    private static final int ELEMENT_NAME = 0;
    private static final int ELEMENT_VALUE = 1;
    private static final int ELEMENT_DESCRIPTION = 2;
    private static final String REPLACE_CLASS_NAME = "@CLASS_NAME@";
    private static final String REPLACE_ELEMENT_TITLE = "@ELEMENT_TITLE@";
    private static final String REPLACE_ELEMENTS = "@ELEMENTS@";
    private static final String REPLACE_MAP_PARAM = "@MAP_PARAM@";
    private static final String REPLACE_METHODS = "@METHODS@";
    private static final String TODAY = "@TODAY@";
    private static final String RN = "\r\n";
    private static final String SP = "    ";

    /**
     * main
     */
    public static void main(String args[]) {
        List<String> templateList = null;

        try {
            // テンプレートを読み込む
            templateList = makeTemplatelist(new File("template"));
        } catch (Exception e) {
            outlog("error.log", "テンプレート読込で例外発生");
            return;
        }

        File[] files = new File("enum_src").listFiles();
        File file = null;
        int okFileCnt = 0;

        // CSVファイルの分javaファイルを生成する
        for (int i = 0; i < files.length; i++) {
            try {
                file = files[i];
                // System.out.println(file.getAbsolutePath());

                if (file.isFile()) {
                    List<String[]> contentList = makeContentList(file);
                    makeEnum(templateList, contentList);

                    okFileCnt++;
                }
            } catch (Exception e) {
                outlog("error.log", "Enum生成で例外発生：" + file.getName());
            }
        }

        outlog("end.log", okFileCnt + "/" + files.length + " files OK");
    }

    /**
     * テンプレート読込
     */
    private static List<String> makeTemplatelist(File file) throws Exception {
        List<String> ret = new ArrayList<>();
        String lineStr = null;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"))) {
            lineStr = br.readLine();

            while (lineStr != null) {
                ret.add(lineStr);
                lineStr = br.readLine();
            }
        }

        return ret;
    }

    /**
     * Enum情報ファイル読込
     */
    private static List<String[]> makeContentList(File file) throws Exception {
        List<String[]> contentList = new ArrayList<>();
        String lineStr = null;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"))) {
            lineStr = br.readLine();

            while (lineStr != null) {
                String[] lineAry = lineStr.split(",");
                contentList.add(lineAry);
                lineStr = br.readLine();
            }
        }

        return contentList;
    }

    /**
     * Enumのjavaファイルを生成
     */
    private static void makeEnum(List<String> templateList, List<String[]> contentList) throws Exception {
        String className = contentList.get(0)[0];
        String elementTitle = contentList.get(0)[1];
        contentList.remove(0);

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream("enum_dest/" + className + ".java"), "UTF-8"))) {

            for (String templateRowStr : templateList) {
                String replacedRowStr = new String(templateRowStr);

                if (templateRowStr.contains(REPLACE_ELEMENTS)) {
                    replacedRowStr = makeElements(contentList);
                }

                if (templateRowStr.contains(TODAY)) {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    String today = sdf.format(c.getTime());
                    replacedRowStr = replacedRowStr.replace(TODAY, today);
                }

                if (templateRowStr.contains(REPLACE_MAP_PARAM)) {
                    String tmp = makeReplaceMapParam(contentList);
                    replacedRowStr = replacedRowStr.replace(REPLACE_MAP_PARAM, tmp);
                }

                if (templateRowStr.contains(REPLACE_METHODS)) {
                    replacedRowStr = makeMethods(className, contentList);
                }

                if (templateRowStr.contains(REPLACE_ELEMENT_TITLE)) {
                    replacedRowStr = replacedRowStr.replace(REPLACE_ELEMENT_TITLE, elementTitle);
                }

                if (templateRowStr.contains(REPLACE_CLASS_NAME)) {
                    replacedRowStr = replacedRowStr.replace(REPLACE_CLASS_NAME, className);
                }

                writer.println(replacedRowStr);
            }
        }
    }

    /**
     * Mapのパラメータを作成
     */
    private static String makeReplaceMapParam(List<String[]> contentList) {
        StringBuilder ret = new StringBuilder();

        for (String[] content : contentList) {
            ret.append(content[ELEMENT_NAME]);
            ret.append(", ");
        }
        ret.delete(ret.length() - 2, ret.length());

        return ret.toString();
    }

    /**
     * Enumの要素を作成
     */
    private static String makeElements(List<String[]> contentList) {
        StringBuilder ret = new StringBuilder();

        for (String[] content : contentList) {
            ret.append(SP).append("/**").append(RN);
            ret.append(SP).append(" * ").append(content[ELEMENT_DESCRIPTION]).append(RN);
            ret.append(SP).append(" */").append(RN);
            ret.append(SP).append(content[ELEMENT_NAME]).append("(\"").append(content[ELEMENT_VALUE]).append("\")").append(",").append(RN);
            ret.append(RN);
        }
        ret.delete(ret.length() - 5, ret.length());
        ret.append(";");

        return ret.toString();
    }

    /**
     * メソッドを作成
     */
    private static String makeMethods(String className, List<String[]> contentList) {
        StringBuilder ret = new StringBuilder();

        for (String[] content : contentList) {
            ret.append(SP).append("/**").append(RN);
            ret.append(SP).append(" * ").append(content[ELEMENT_DESCRIPTION]).append("であるか判別する").append(RN);
            ret.append(SP).append(" * ").append(RN);
            ret.append(SP).append(" * @return ").append(content[ELEMENT_DESCRIPTION]).append("の場合は{@code true}、その他は{@code false}").append(RN);
            ret.append(SP).append(" */").append(RN);
            ret.append(SP).append("public boolean ").append(snakeToCamel("IS_" + content[ELEMENT_NAME])).append("() {").append(RN);
            ret.append(SP).append(SP).append("return this == ").append(content[ELEMENT_NAME]).append(";").append(RN);
            ret.append(SP).append("}").append(RN);
            ret.append(RN);
        }
        ret.delete(ret.length() - 4, ret.length());

        return ret.toString();
    }

    /**
     * スネークケースをローワーキャメルケースへ
     */
    public static String snakeToCamel(String targetStr) {
        Pattern p = Pattern.compile("_([a-z])");
        Matcher m = p.matcher(targetStr.toLowerCase());

        StringBuffer sb = new StringBuffer(targetStr.length());
        while (m.find()) {
            m.appendReplacement(sb, m.group(1).toUpperCase());
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * ログ出力
     */
    private static void outlog(String fileName, String str) {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName, true), "UTF-8"))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(System.currentTimeMillis()));
            writer.println(timestamp + " > " + str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
