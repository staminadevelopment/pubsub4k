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

package pw.stamina.pubsub4k.publish

import pw.stamina.pubsub4k.subscribe.Subscription

interface MutablePublisher<T : Any> : Publisher<T> {

    /** Adds the [subscription] to this publisher. */
    fun add(subscription: Subscription<T>)

    /** Removes the [subscription] from this publisher. */
    fun remove(subscription: Subscription<T>)

    /** Clears all the [subscriptions] from this publisher. */
    fun clear()
}