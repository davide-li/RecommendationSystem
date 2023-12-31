import scala.collection.mutable

object TfIdfCalc {

  /**
   * inverse document frequency
   * 逆文档频率 = log (语料库文档总数 / 包含该词的文档书 + 1)
   * Inverse document frequency = log (total number of corpus documents / number of documents containing the word + 1)
   *
   * @param query List
   * @param dataset
   * @return Map docCount
   */
  def idfCalc(query: List[String], dataset: List[mutable.Map[String, String]]): mutable.Map[String, Double] = {
    val size = dataset.length
    val docCount: mutable.Map[String, Double] = mutable.Map.empty
    query.foreach( q =>
      docCount(q)= dataset.count(entry =>
        entry.values.mkString(" ").toLowerCase.contains(q)
    ))
    // Calculate IDF
    val idfMap = docCount.map(entry => {
      if (entry._2 > 0) (entry._1, Math.log(size / entry._2))
      else (entry._1, 0.0)
    })
    idfMap
  }


  /**
   * function of tf_calc: memorize a type of table in a file
   * term frequency 词频 = 某个词在文章中间出现的次数 / 文章的总词数
   *
   * @param : query: List[String], list of keyword
   *        row : The text after splicing the data of each row with spaces
   * @return : mutable.Map[String, Double] -> Map(kWord -> tf_value, ...)
   */
  def tfCalc(query: List[String], row: String): mutable.Map[String, Double] = {
    val docSize: Double = row.split(" ").length.toDouble
    val wCounter = WordUtil.wordCount(row, query)
    val tf = mutable.Map.empty[String, Double].withDefaultValue(0.0)

    query.foreach { q =>
      val wordFreq = wCounter.getOrElse(q, 0).toDouble
      val normFreq = wordFreq / docSize
      tf(q) = normFreq
    }
    tf
  }


  /**
   * Ranking and the weights value of keywords
   * A high weight in tf–idf is reached by a high term frequency (in the given document) and a low document frequency
   * of the term in the whole collection of documents; the weights hence tend to filter out common terms.
   * tf-idf = TF * IDF
   *
   * @param query
   * @param dataset
   */
  def tfIdfCalc(query: List[String], dataset: List[mutable.Map[String, String]]): Unit = {
    val idf = idfCalc(query, dataset)
    val ranks = new Array[Double](dataset.length)
    for (i <- 0 until dataset.length) {
      var tfIdf = 0.0
      val text = constant.Columns.map(column => dataset(i)(column)).mkString(" ")
      val tf = tfCalc(query, text)
      query.foreach(q => {
        var a = idf(q)
        var b = tf(q)
        tfIdf += a * b
      })
      println(text)
      println(tfIdf)
      ranks(i) = tfIdf
      if (tfIdf > 0) {
        println(f"Title: ${dataset(i)(constant.Columns(0))} \t Weights value: $tfIdf%.6f")
      }
    }

    val posList = (0 until dataset.length).toList
      .sortWith((i, j) => ranks(i) > ranks(j))
      .take(10)
    println(" \n ---------------------Rank--------------------- \n ")
    for ((pos, j) <- posList.zipWithIndex) {
      printf("%d. %s%n", j + 1, dataset(pos)(constant.Columns(0)))
    }
  }

}



