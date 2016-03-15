import scala.reflect._

trait Nationality
class Italian extends Nationality { override def toString() = "Italian" }
class Chinese extends Nationality { override def toString() = "Chinese" }
class Japanese extends Nationality { override def toString() = "Japanese" }
class Korean extends Nationality { override def toString() = "Korean" }

trait Restaurant {
 def feed(customer: Nationality): String
 def status(customer: String, dishes: String) =
   "Trying to feed a %s at a restaurant serving %s".format(customer, dishes)
}

class Restaurant1Class[N1 <: Nationality](implicit t1: ClassTag[N1]) extends Restaurant {
 def feed(customer: Nationality) = {
   status(customer.toString, t1.toString)
 }
}
trait Restaurant1[N1 <: Nationality] extends Restaurant1Class[N1]

class Restaurant2Class[N1 <: Nationality, N2 <: Nationality](
 implicit t1: ClassTag[N1], t2: ClassTag[N2]) extends Restaurant
{
 def feed(customer: Nationality) = {
   status(customer.toString, List(t1, t2).mkString(", "))
 }
}
trait Restaurant2[N1 <: Nationality, N2 <: Nationality] extends Restaurant2Class[N1, N2]

object andrew
{
 def main(args: Array[String]): Unit = {
   val restaurants = List(new Restaurant1[Chinese] {}, new Restaurant2[Chinese, Japanese] {})
   val customer = new Japanese {}
   restaurants map (_ feed customer) foreach println
 }
}

