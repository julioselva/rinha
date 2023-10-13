package lib.zredis

import lib.zconfig.ZConfig
import zio._
import zio.redis._
import zio.schema.Schema
import zio.schema.codec._

object ZRedis {

  type ZRedis = Redis

  private val redisExecutor: ZLayer[Any, RedisError.IOError, RedisExecutor] =
    ZLayer(ZIO.config[RedisConfig](ZConfig.config.map(_.redis)).orDie) >>> RedisExecutor.layer

  private val codecSupplier: ZLayer[Any, Nothing, CodecSupplier] =
    ZLayer.succeed[CodecSupplier](ProtobufCodecSupplier)

  val layer: ZLayer[Any, RedisError.IOError, Redis] = (redisExecutor ++ codecSupplier) >>> Redis.layer

}

object ProtobufCodecSupplier extends CodecSupplier {
  def get[A: Schema]: BinaryCodec[A] = ProtobufCodec.protobufCodec

}
