/*
 * Copyright © 2020-2029 organization opcooc
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
package com.opcooc.storage.args;

import java.util.concurrent.TimeUnit;

import com.amazonaws.HttpMethod;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * @author shenqicheng
 * @since 1.2.0
 */
@Getter
@SuperBuilder(toBuilder = true)
public class GetPresignedObjectUrlArgs extends ObjectArgs {
    //defaults to 15 minutes
    public static final long DEFAULT_EXPIRY_TIME = 15;
    //max to 7 days
    public static final long MAX_EXPIRY_TIME = 60 * 24 * 7L;

    private HttpMethod method;

    private boolean specType;

    @Builder.Default
    private long expiry = DEFAULT_EXPIRY_TIME;

    @Override
    public void validate() {
        super.validate();
        validateExpiry(this.expiry);
    }

    private void validateExpiry(long expiry) {
        if (expiry < 1 || expiry > MAX_EXPIRY_TIME) {
            throw new IllegalArgumentException(
                    "expiry must be minimum 1 second to maximum "
                            + TimeUnit.DAYS.toMinutes(MAX_EXPIRY_TIME)
                            + " days");
        }
    }
}
