import org.apache.calcite.sql.advise.SqlSimpleParser;
import org.apache.calcite.sql.parser.SqlParser;

public class Reproduce {

    public static boolean equalTo(String originSql, String actual, String expect) {
        if (!actual.equals(expect)) {
            System.out.println("origin :" + originSql);
            System.out.println("expect :" + expect);
            System.out.println("actual :" + actual + "\n");
        }
        return true;
    }

    /**
     * the SqlSimpleParser#simplifySql can filter comments,
     * but when -- follow by variable, this comment will not be filter. But in fact, it should be filtered
     */
    public static void main(String[] args) {
        SqlSimpleParser simpleParser =
                new SqlSimpleParser("_suggest_", SqlParser.Config.DEFAULT);

        final String originSql = "select * from a ";
        final String resultSql = "SELECT * FROM a ";

        // when SqlSimpleParser.Tokenizer#nextToken() method parse sql,
        // ignore the  "--" after 10.0, this is a comment,
        // but Tokenizer#nextToken() does not recognize it
        {
            String sqlWithComment = originSql + "where price > 10.0-- this is comment \n"
                    + " -- comment ";
            String actualSql = simpleParser.simplifySql(sqlWithComment);
            equalTo(sqlWithComment, actualSql, resultSql + "WHERE price > 10.0");
        }

        {
//            String sqlWithOutComment = originSql + "where column_b='/* this is not comment */'";
            String sqlWithOutComment = "select * from a where column_b= '/* this is not comment */'";
            String actualSql = simpleParser.simplifySql(sqlWithOutComment);
            equalTo(sqlWithOutComment, actualSql, resultSql + "WHERE column_b= '/* this is not comment */'");
        }

        {
            String sqlWithOutComment = originSql + "where column_b='2021 --this is not comment'";
            String actualSql = simpleParser.simplifySql(sqlWithOutComment);
            equalTo(sqlWithOutComment, actualSql, resultSql + "WHERE column_b= '2021 --this is not comment'");
        }

        {
            String sqlWithOutComment = originSql + "where column_b='2021--this is not comment'";
            String actualSql = simpleParser.simplifySql(sqlWithOutComment);
            equalTo(sqlWithOutComment, actualSql, resultSql + "WHERE column_b= '2021--this is not comment'");
        }
    }
}
