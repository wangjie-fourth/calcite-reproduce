import org.apache.calcite.sql.advise.SqlSimpleParser;
import org.apache.calcite.sql.parser.SqlParser;

public class Reproduce {

    /**
     * the SqlSimpleParser#simplifySql can filter comments,
     * but when -- follow by variable, this comment will not be filter. But in fact, it should be filtered
     */
    public static void main(String[] args) {
        String sqlWithComment = "select count(*) from a where price > 10.0-- \n" +
                " -- comment ";
        SqlSimpleParser simpleParser = new SqlSimpleParser("_suggest_", SqlParser.Config.DEFAULT);
        String sqlWithOutComment = simpleParser.simplifySql(sqlWithComment);
        System.out.println("sqlWithOutComment = " + sqlWithOutComment);
    }
}
