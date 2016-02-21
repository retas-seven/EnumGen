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
        List<String> templateContentList = null;
        List<String[]> csvContentList = null;

        // テンプレートファイルの内容を取込む
        templateContentList = importTempleteFileContent();

        // CSVファイルが格納してあるディレクトリの内容を取得する
        File[] sourceDirContents = getSourceDirContents();

        // テンプレートとCSVファイルの内容をもとにEnumのjavaファイルを出力する
        for (File sourceDirContent : sourceDirContents) {
            if (sourceDirContent.isFile()) {
                csvContentList = importCsvContent(sourceDirContent);
                makeEnumSourceFile(templateContentList, csvContentList);
            }
        }
    }

    /**
     * Enum定義CSVファイル格納ディレクトリの内容を取得
     */
    private static File[] getSourceDirContents() {
        File[] sourceDirContents = new File("enum_source").listFiles();
        return sourceDirContents;
    }

    /**
     * テンプレート読込
     */
    private static List<String> importTempleteFileContent() {
        List<String> templateContentRowList = new ArrayList<>();
        String templateContentRow = null;

        try (BufferedReader templateReader = new BufferedReader(new FileReader(new File("template")))) {
            templateContentRow = templateReader.readLine();

            while (templateContentRow != null) {
                templateContentRowList.add(templateContentRow);
                templateContentRow = templateReader.readLine();
            }
        } catch (IOException e) {
            outlog("error.log", "テンプレート読込で例外発生");
            throw new RuntimeException(e.getMessage());
        }
        return templateContentRowList;
    }

    /**
     * Enum定義CSVファイル読込
     */
    private static List<String[]> importCsvContent(File file) {
        List<String[]> csvContentRowList = new ArrayList<>();
        String csvContentRow = null;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            csvContentRow = br.readLine();

            while (csvContentRow != null) {
                String[] lineAry = csvContentRow.split(",");
                csvContentRowList.add(lineAry);
                csvContentRow = br.readLine();
            }
        } catch (IOException e) {
            outlog("error.log", "CSV読込で例外発生");
            throw new RuntimeException(e.getMessage());
        }

        return csvContentRowList;
    }

    /**
     * Enumのjavaファイルを生成
     */
    private static void makeEnumSourceFile(List<String> templateContentRowList, List<String[]> csvContentList) {
        String enumClassName = csvContentList.get(0)[0];
        String enumClassJapaneseName = csvContentList.get(0)[1];
        csvContentList.remove(0);

        try (PrintWriter enumFileWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
                "enum_destination/" + enumClassName + ".java"), "UTF-8"))) {

            for (String templateContentRow : templateContentRowList) {
                String replacedRowStr = new String(templateContentRow);

                if (templateContentRow.contains(REPLACE_ELEMENTS)) {
                    replacedRowStr = makeElements(csvContentList);
                }

                if (templateContentRow.contains(TODAY)) {
                    replacedRowStr = replacedRowStr.replace(TODAY, makeTodayString());
                }

                if (templateContentRow.contains(REPLACE_MAP_PARAM)) {
                    replacedRowStr = replacedRowStr.replace(REPLACE_MAP_PARAM, makeReplaceMapParam(csvContentList));
                }

                if (templateContentRow.contains(REPLACE_METHODS)) {
                    replacedRowStr = makeMethods(enumClassName, csvContentList);
                }

                if (templateContentRow.contains(REPLACE_ELEMENT_TITLE)) {
                    replacedRowStr = replacedRowStr.replace(REPLACE_ELEMENT_TITLE, enumClassJapaneseName);
                }

                if (templateContentRow.contains(REPLACE_CLASS_NAME)) {
                    replacedRowStr = replacedRowStr.replace(REPLACE_CLASS_NAME, enumClassName);
                }

                enumFileWriter.println(replacedRowStr);
            }
        } catch (IOException e) {
            outlog("error.log", "Enumファイル生成で例外発生");
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 日付の文字列を作成
     */
    private static String makeTodayString() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String today = sdf.format(c.getTime());
        return today;
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
            ret.append(SP).append("/** ").append(content[ELEMENT_DESCRIPTION]).append(" */").append(RN);
            ret.append(SP).append(content[ELEMENT_NAME]).append("(\"").append(content[ELEMENT_VALUE]).append("\")")
                    .append(",").append(RN);
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
            ret.append(SP).append(" * ").append(content[ELEMENT_DESCRIPTION]).append("であるか判別する<BR>").append(RN);
            ret.append(SP).append(" * ").append(RN);
            ret.append(SP).append(" * @return ").append(content[ELEMENT_DESCRIPTION]).append("の場合は{@code true}、それ以外は{@code false}").append(RN);
            ret.append(SP).append(" **/").append(RN);
            ret.append(SP).append("public boolean ").append(snakeToCamel("IS_" + content[ELEMENT_NAME])).append("() {").append(RN);
            ret.append(SP).append(SP).append("return this == ").append(content[ELEMENT_NAME]).append(";").append(RN);
            ret.append(SP).append("}").append(RN);
            ret.append(RN);
        }
        ret.delete(ret.length() - 4, ret.length());

        return ret.toString();
    }

    /**
     * スネークケース表記をローワーキャメルケース表記へ
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
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(System
                    .currentTimeMillis()));
            writer.println(timestamp + " > " + str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
