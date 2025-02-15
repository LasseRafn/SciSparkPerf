/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dia.macrobench.core

import org.openjdk.jmh.annotations.{Scope, State}

import org.apache.spark.rdd.RDD
import org.apache.spark.SparkConf

import org.dia.core.SciSparkContext

@State(Scope.Benchmark)
object BenchmarkContext {
  val properties = scala.io.Source.fromFile("Properties").mkString.split("\n").filter(p => p != "")
  val properties_map = properties.map(p => p.split(" +")).map(p => (p(0), p(1))).toMap


  var fspath = properties_map("fs.base.path")
  var cxtURI = properties_map("spark.master")
  var partitionCount = properties_map("spark.cores.max").toInt * 3
  val mccvar = properties_map("mcc.variable")
  val srddfunctionsvar = properties_map("srddfunc.variable")
  val sparkConf = new SparkConf()
    .setMaster(cxtURI)
    .setAppName("SciSparkContextBenchmark")
    .set("spark.executor.uri", properties_map("spark.executor.uri"))
    .set("spark.cores.max", properties_map("spark.cores.max"))
    .set("spark.executor.memory", properties_map("spark.executor.memory"))

  var sc : SciSparkContext = new SciSparkContext(sparkConf)
  sc.sparkContext.addJar("target/scala-2.11/SciSparkPerf.jar")

  def evaluate[T](rdd : RDD[T]) : Array[Unit] = {
    rdd.sparkContext.runJob(rdd, (iter : Iterator[T]) => {
      while (iter.hasNext) iter.next()
    })
  }

}
