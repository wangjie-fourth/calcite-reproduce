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

    // SqlSimpleParser.Tokenizer#nextToken() lines 401 - 423
    // is used to recognize the sql of TokenType.ID or some keywords
    // if a certain segment of characters is continuously composed of Token,
    // the function of this code may be wrong
    // E.g :
    // (1)select * from a where price> 10.0--comment
    // 【10.0--comment】should be recognize as TokenType.ID("10.0") and TokenType.COMMENT
    // but it recognize as TokenType.ID("10.0--comment")
    // (2)select * from a where column_b='/* this is not comment */'
    // 【/* this is not comment */】should be recognize as
    // TokenType.SQID("/* this is not comment */"), but it was not
    public static void main(String[] args) {
        SqlSimpleParser simpleParser =
                new SqlSimpleParser("_suggest_", SqlParser.Config.DEFAULT);

        final String originSql = "select * from a ";
        final String resultSql = "SELECT * FROM a ";

        {
            String sqlWithComment = originSql + "where price > 10.0-- this is comment \n"
                    + " -- comment ";
            String actualSql = simpleParser.simplifySql(sqlWithComment);
            equalTo(sqlWithComment, actualSql, resultSql + "WHERE price > 10.0");
        }

        {
            String sqlWithOutComment = originSql + "where column_b='/* this is not comment */'";
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
