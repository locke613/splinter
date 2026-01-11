import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.udf

object DemoApp {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder.getOrCreate()
    import spark.implicits._
    
    val rdd = spark.sparkContext.parallelize(Seq(1, 2, 3))
    val df = Seq((1, "a"), (2, "b")).toDF("id", "value")
    val otherDf = Seq((1, "x")).toDF("id", "other")
    
    // Bad patterns
    val data = rdd.collect()
    
    if (rdd.count() == 0) {
      println("Empty")
    }

    // Tuple Access
    val tupleRdd = rdd.map(x => (x, x * 2))
    tupleRdd.map(t => t._1 + t._2)

    // Deep Nesting
    if (true) {
      if (true) {
        if (true) {
          if (true) {
             println("Too deep")
          }
        }
      }
    }

    // UDFs
    val simpleUdf = udf((i: Int) => i + 1)
    df.select(simpleUdf($"id"))

    // Filter after Join
    df.join(otherDf, "id").filter($"id" > 1)

    // Deprecated Union
    df.unionAll(otherDf)

    // Prefer Find
    val list = Seq(1, 2, 3, 4)
    list.filter(x => x > 2).headOption

    // Avoid Var Update
    var loopVar = 0
    for (i <- 0 to 10) {
      loopVar += 1
    }
    if (loopVar > 5) {
      loopVar = 0
    }
  }

  // Avoid Data Traits (Anti-Pattern)
  trait PersonLike {
    def name: String
    def age: Int
  }

  // Avoid Catching Throwable
  try {
    // do something
  } catch {
    case t: Throwable => println("Caught everything")
  }

  def veryLongMethod(): Unit = {
    println("line 1")
    println("line 2")
    println("line 3")
    println("line 4")
    println("line 5")
    println("line 6")
    println("line 7")
    println("line 8")
    println("line 9")
    println("line 10")
    println("line 11")
    println("line 12")
    println("line 13")
    println("line 14")
    println("line 15")
    println("line 16")
    println("line 17")
    println("line 18")
    println("line 19")
    println("line 20")
    println("line 21")
    println("line 22")
    println("line 23")
    println("line 24")
    println("line 25")
    println("line 26")
    println("line 27")
    println("line 28")
    println("line 29")
    println("line 30")
    println("line 31")
    println("line 32")
    println("line 33")
    println("line 34")
    println("line 35")
    println("line 36")
    println("line 37")
    println("line 38")
    println("line 39")
    println("line 40")
    println("line 41")
    println("line 42")
    println("line 43")
    println("line 44")
    println("line 45")
    println("line 46")
    println("line 47")
    println("line 48")
    println("line 49")
    println("line 50")
    println("line 51")
  }

  // Large Tuples
  def largeTupleMethod(): (Int, Int, Int, Int, Int) = {
    (1, 2, 3, 4, 5)
  }

  // Avoid Return
  def explicitReturn(x: Int): Int = {
    return x + 1
  }

  // Avoid Var
  def mutableState(): Unit = {
    var count = 0
  }

  // Avoid Null
  def nullUsage(): Unit = {
    val x: String = null
  }

  // Avoid Option.get
  def optionGetUsage(opt: Option[Int]): Unit = {
    val x = opt.get
  }

  // Avoid Head
  def headUsage(seq: Seq[Int]): Unit = {
    val x = seq.head
  }
}