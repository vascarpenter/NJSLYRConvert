// 忍殺語変換をアシストするソフト
// フーリンカザンがダウンロードできなくなっったため　辞書のみ利用し自作

// 形態素解析： kuromoji © アティリカ株式会社 https://github.com/atilika/kuromoji/
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

fun main(args: Array<String>)
{
    var conn: Connection? = null

    // 変換辞書をsqliteから読みます
    try
    {
        conn = DriverManager.getConnection("jdbc:sqlite::resource:NJSLYRDict.sqlite")
        val statement: Statement? = conn?.createStatement()
        val resultSet: ResultSet? = statement?.executeQuery("SELECT name,value FROM dialect")
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
                it.allFeaturesArray[0] + "," + it.allFeaturesArray[1] + "," + it.allFeaturesArray[4] + "," + it.allFeaturesArray[5] + "," + it.allFeaturesArray[6]

            newtext += "＜" + surhi + "＞" + sur
        }
        var oldoldtext = newtext

        // replaceできる限り、忍殺語辞書に基づき変換
        var replace = true
        do
        {
            replace = false
            nindb.forEach {
                if (newtext.indexOf(it.name) >= 0)
                {
                    newtext = newtext.replace(it.name, it.value)
                    replace = true
                }
            }
        }
        while (replace)

        if(g.oldShowCheckBox.isSelected)
        {
            newtext = oldoldtext + "\n▼▼変換な▼▼\n\n" + newtext
        }

        // 品詞の＜＞を削除するかどうか
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
