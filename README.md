SlickCats
==========

[Cats](https://github.com/typelevel/cats) instances for [Slick's](http://slick.typesafe.com/) `DBIO` including:
* Monad
* MonadError
* CoflatMap
* Group
* Monoid
* Semigroup
* Comonad
* Order
* PartialOrder
* Equals

## Using
To add *slick-cats* dependency to a project, add the following to your build definition:
```scala
libraryDependencies += "com.rms.miu" %% "slick-cats" % version
```

Because of possible binary incompatibilities, here are the dependency versions used in each release:

| slick-cats version | slick version | cats version |
|:------------------:|:-------------:|:------------:|
|       0.10.5       |     3.4.1     |    2.9.0     |
|       0.10.4       |     3.3.3     |    2.3.1     |
|       0.10.3       |     3.3.2     |    2.2.0     |
|       0.10.2       |     3.3.2     |    2.1.0     |
|       0.10.1       |     3.3.2     |    2.0.0     |

Artifacts are publicly available on Maven Central starting from version *0.6*.

## Accessing the Instances
Some or all of the following imports may be needed:
```scala
import cats._
import slick.dbio._
import com.rms.miu.slickcats.DBIOInstances._
```
Additionally, be sure to have an implicit `ExecutionContext` in scope. The implicit conversions require it
and will fail with non-obvious errors if it's missing.
```scala
implicitly[Monad[DBIO]]
// error:
// No given instance of type cats.Monad[slick.dbio.DBIO] was found for parameter e of method implicitly in object Predef.
// I found:
// 
//     com.rms.miu.slickcats.DBIOInstances.dbioInstance(
//       /* missing */summon[scala.concurrent.ExecutionContext])
// 
// But no implicit values were found that match type scala.concurrent.ExecutionContext.
// def monad[F[_] : Monad, A](fa: F[A]): F[A] = fa
//
```

```scala
import scala.concurrent.ExecutionContext.Implicits.global
```

instances will be available for:
```scala
implicitly[Monad[DBIO]]
implicitly[MonadError[DBIO, Throwable]]
implicitly[CoflatMap[DBIO]]
implicitly[Functor[DBIO]]
implicitly[Applicative[DBIO]]
```

If a Monoid exists for `A`, here taken as Int, then the following is also available
```scala
implicitly[Group[DBIO[Int]]]
implicitly[Semigroup[DBIO[Int]]]
implicitly[Monoid[DBIO[Int]]]
```

## Known Issues
Instances are supplied for `DBIO[A]` only. Despite being the same thing,
type aliases will not match for implicit conversion. This means that the following

```scala
def monad[F[_] : Monad, A](fa: F[A]): F[A] = fa

val fail1: DBIOAction[String, NoStream, Effect.All] = DBIO.successful("hello")
// fail1: DBIOAction[String, NoStream, All] = SuccessAction(value = "hello")
val fail2 = DBIO.successful("hello")
// fail2: DBIOAction[String, NoStream, Effect] = SuccessAction(value = "hello")
val success: DBIO[String] = DBIO.successful("hello")
// success: DBIOAction[String, NoStream, All] = SuccessAction(value = "hello")
```
will _not_ compile
```scala
monad(fail1)
monad(fail2)
// error:
// Found:    (repl.MdocSession.MdocApp.fail1 :
//   slick.dbio.DBIOAction[String, slick.dbio.NoStream, slick.dbio.Effect.All])
// Required: ([_] =>> Any)[Any]
// Note that implicit conversions were not tried because the result of an implicit conversion
// must be more specific than ([_] =>> Any)[Any]
// monad(fail1)
//       ^^^^^
// error:
// Found:    (repl.MdocSession.MdocApp.fail2 :
//   slick.dbio.DBIOAction[String, slick.dbio.NoStream, slick.dbio.Effect])
// Required: ([_] =>> Any)[Any]
// Note that implicit conversions were not tried because the result of an implicit conversion
// must be more specific than ([_] =>> Any)[Any]
// monad(fail2)
//       ^^^^^
```
but
```scala
monad(success)
// res10: DBIOAction[String, NoStream, All] = SuccessAction(value = "hello")
```
will compile fine.

## Extras
This README is compiled using [mdoc](https://scalameta.org/mdoc/) to ensure that only working examples are given.
