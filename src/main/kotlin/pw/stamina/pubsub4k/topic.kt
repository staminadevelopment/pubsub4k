/*
 * Copyright 2019 Stamina Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pw.stamina.pubsub4k

typealias Topic<T> = Class<T>

/**
 * Returns `true` if the [other] topic is a subclass/subtopic
 * of this topic, otherwise returns `false`.
 *
 * A topic is a subtopic of another, if itself is
 * [Class.isAssignableFrom] from the other topic.
 */
fun Topic<*>.isSubtopicOf(other: Topic<*>) = other.isAssignableFrom(this)