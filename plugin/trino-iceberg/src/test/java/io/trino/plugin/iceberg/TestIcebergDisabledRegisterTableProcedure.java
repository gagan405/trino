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
package io.trino.plugin.iceberg;

import io.trino.testing.AbstractTestQueryFramework;
import io.trino.testing.QueryRunner;
import org.junit.jupiter.api.Test;

public class TestIcebergDisabledRegisterTableProcedure
        extends AbstractTestQueryFramework

{
    @Override
    protected QueryRunner createQueryRunner()
            throws Exception
    {
        return IcebergQueryRunner.builder()
                .build();
    }

    @Test
    public void testDisabledRegisterTableProcedure()
    {
        assertQueryFails("CALL iceberg.system.register_table (CURRENT_SCHEMA, 'test_table', '/var/test/location/test_table/')",
                "register_table procedure is disabled");
    }
}
