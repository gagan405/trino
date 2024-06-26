/*
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
package io.trino.sql.planner.iterative.rule;

import com.google.common.collect.ImmutableList;
import io.trino.sql.planner.Symbol;
import io.trino.sql.planner.iterative.rule.test.BaseRuleTest;
import org.junit.jupiter.api.Test;

import static io.trino.sql.planner.assertions.PlanMatchPattern.limit;
import static io.trino.sql.planner.assertions.PlanMatchPattern.offset;
import static io.trino.sql.planner.assertions.PlanMatchPattern.sort;
import static io.trino.sql.planner.assertions.PlanMatchPattern.values;
import static io.trino.sql.tree.SortItem.NullOrdering.FIRST;
import static io.trino.sql.tree.SortItem.Ordering.ASCENDING;

public class TestPushLimitThroughOffset
        extends BaseRuleTest
{
    @Test
    public void testPushdownLimitThroughOffset()
    {
        tester().assertThat(new PushLimitThroughOffset())
                .on(p -> p.limit(
                        2,
                        p.offset(5, p.values())))
                .matches(
                        offset(
                                5,
                                limit(7, values())));
    }

    @Test
    public void testPushdownLimitWithTiesThroughOffset()
    {
        tester().assertThat(new PushLimitThroughOffset())
                .on(p -> {
                    Symbol a = p.symbol("a");
                    return p.limit(
                            2,
                            ImmutableList.of(a),
                            p.offset(5, p.values(a)));
                })
                .matches(
                        offset(
                                5,
                                limit(7, ImmutableList.of(sort("a", ASCENDING, FIRST)), values("a"))));
    }

    @Test
    public void doNotPushdownWhenRowCountOverflowsLong()
    {
        tester().assertThat(new PushLimitThroughOffset())
                .on(p -> {
                    return p.limit(
                            Long.MAX_VALUE,
                            p.offset(Long.MAX_VALUE, p.values()));
                })
                .doesNotFire();
    }

    @Test
    public void testPushdownWithPreSortedSymbolsThroughOffset()
    {
        tester().assertThat(new PushLimitThroughOffset())
                .on(p -> p.limit(
                        2,
                        false,
                        ImmutableList.of(p.symbol("a")),
                        p.offset(5, p.values())))
                .matches(
                        offset(
                                5,
                                limit(7, ImmutableList.of(), false, ImmutableList.of("a"), values())));
    }
}
