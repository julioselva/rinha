package lib.zredis

import lib.zconfig.ZConfig
import zio._
import zio.redis._
import zio.schema.Schema
import zio.schema.codec._

object ZRedis {

  type ZRedis = Redis

  private val redisConfig: ZLayer[Any, RedisError.IOError, RedisConfig] =
    ZLayer(ZIO.config[RedisConfig](ZConfig.config.map(_.redis)).orDie)

  private val codecSupplier: ZLayer[Any, Nothing, CodecSupplier] =
    ZLayer.succeed[CodecSupplier](ProtobufCodecSupplier)

  val layer: ZLayer[Any, RedisError.IOError, Redis] = (redisConfig ++ codecSupplier) >>> Redis.singleNode

}

object ProtobufCodecSupplier extends CodecSupplier {
  def get[A: Schema]: BinaryCodec[A] = ProtobufCodec.protobufCodec
}
