// 忍殺語変換をアシストするソフト
// フーリンカザンがダウンロードできなくなった

// 形態素解析： kuromoji © アティリカ株式会社 https://github.com/atilika/kuromoji/
// sqlite-jdbc https://github.com/xerial/sqlite-jdbc
// SwingパーツのフォントをDialog 12pt regularにしましたか


import org.atilika.kuromoji.Token
import org.atilika.kuromoji.Tokenizer
import javax.swing.JFrame
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.sql.Connection

data class NinDB(var name: String, var value: String)

val nindb = mutableListOf<NinDB>()

// 実際アスタリスクは正規表現に使われるため置換が必要な
fun avoidasterisk(arg: String) : String
{
    if(arg == "*")
        return ""
    else
        return arg
}

fun main(args: Array<String>)
{
    var conn: Connection? = null

    // 変換辞書をsqliteから読みます
    try
    {
        conn = DriverManager.getConnection("jdbc:sqlite::resource:NJSLYRDict.sqlite")
        val statement: Statement? = conn?.createStatement()
        val resultSet: ResultSet? = statement?.executeQuery("SELECT name,value FROM dialect order by id")
        while (resultSet?.next() == true)
        {
            val name = resultSet.getString(1)
            val value = resultSet.getString(2)
            val unit = NinDB(name, value)
            nindb.add(unit)
        }
        resultSet?.close()
        statement?.close()
    }
    catch (ex: Exception)
    {
        ex.printStackTrace()
    }
    finally
    {
        if (conn != null)
        {
            conn.close()
        }
    }
    //  System.out.println(nindb)

    // Windowの作成
    val f = JFrame("忍殺語変換")
    val g = gui()
    f.contentPane = g.panel
    f.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    f.setSize(600, 480)
    f.isResizable = true
    f.setLocationRelativeTo(null)
    f.isVisible = true

    g.convertButton.addActionListener {
        var oldtext = g.origText.text

        // 形態素解析
        val tokenizer = Tokenizer.builder().build()
        val tokens: List<Token> = tokenizer.tokenize(oldtext)
        var newtext = ""
        tokens.forEach {
            var sur = it.surfaceForm
            var surhi =
                avoidasterisk(it.allFeaturesArray[0]) + ","+
                avoidasterisk(it.allFeaturesArray[1]) + ","+
                avoidasterisk(it.allFeaturesArray[4]) + ","+
                avoidasterisk(it.allFeaturesArray[5]) + ","+
                avoidasterisk(it.allFeaturesArray[6])

            newtext += "＜" + surhi + "＞" + sur
        }
        var oldoldtext = newtext

        // replaceできる限り、忍殺語辞書に基づき変換 正規表現に変更
        var replace = true
        do
        {
            replace = false
            nindb.forEach {
                val regex = Regex(it.name)
                while (regex.containsMatchIn(newtext))
                {
                    newtext = regex.replace(newtext, it.value)
                    replace = true
                }
            }
        }
        while (replace)

        if(g.oldShowCheckBox.isSelected)
        {
            newtext = oldoldtext + "\n▼▼変換な▼▼\n\n" + newtext
        }

        // 品詞の＜＞を削除する
        if(! g.hinsiCheckBox.isSelected)
        {
            do
            {
                if (newtext.indexOf("＜") >= 0)
                {
                    newtext = newtext.replace(Regex("＜[^＞]+＞"), "")
                    replace = true
                }
                else
                {
                    replace = false
                }
            }
            while(replace)
        }
        g.newText.text = newtext
    }
}
