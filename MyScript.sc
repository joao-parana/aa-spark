import $ivy.`com.softwaremill.sttp::core:1.0.5`
import com.softwaremill.sttp._
implicit val backend = HttpURLConnectionBackend()
# sttp.get(uri"http://httpbin.org/ip").send()

import $ivy.{
          `org.apache.spark::spark-core:2.2.0`,
          `org.apache.spark::spark-sql:2.2.0`,
          `org.apache.spark::spark-yarn:2.2.0`
  }
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.SparkSession

val conf1 = new SparkConf().setMaster("local[1]").setAppName("app1With1Core")
val conf2 = new SparkConf().setMaster("local[2]").setAppName("app1With2Core")
val conf3 = new SparkConf().setMaster("local[3]").setAppName("app1With3Core")
val conf4 = new SparkConf().setMaster("local[4]").setAppName("app1With4Core")

val sc = new SparkContext(conf4)
val sqlContext = new SQLContext(sc)
