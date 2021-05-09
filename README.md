# NJSLYRConvert

- Java Swing で書かれた忍殺語変換プログラム（の予定）
- 忍殺 NinjaSlayer とはなにか
  - Twitterの公式アカウント @njslyr
  - 公式facebook https://www.facebook.com/ninjaslayer.jp
  - https://diehardtales.com
    
- ビルドに必要
  - kotlin  IntelliJ IDEA
  - `kuromoji-0.7.7.jar` 
  - `sqlite-jdbc-3.34.0.jar`
  - TablePlusなどのsqliteエディタ

- 実行に必要な環境
  - Java 1.8以降 
  - Sqlite3
    - MacOSXはシステムプレインストールなので何もいらない
    - Windowsは公式 https://www.sqlite.org/download.html から sqlite-dllをダウンロードして DLL と cfg を `\Windows\System` (64bit) あるいは `\Windows\System32` (32bit)に入れる

- 実行方法
  - `java -jar NJSLYRConvert.jar`
  - windows,macなら jarダブルクリックでok
  
- 製作
  - いまのところ形態素解析くらいしかできていません 
  - 変換辞書がしょぼすぎ＆機能が乏しすぎるのでC#で作られ現在ではダウンロード不可
  となっているフーリンカザン作者に問い合わせをしています
