/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ljh.gtd3.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ljh.gtd3.data.StuffsSource.StuffsLocalDataSource;
import com.ljh.gtd3.data.StuffsSource.StuffsRepository;
import com.ljh.gtd3.data.StuffsSource.remote.StuffsRemoteDataSource;
import com.ljh.gtd3.data.UsersSource.UsersLocalDataSource;
import com.ljh.gtd3.data.UsersSource.UsersRepository;
import com.ljh.gtd3.data.UsersSource.remote.UsersRemoteDataSource;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Enables injection of mock implementations for
 * at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
public class Injection {

    public static UsersRepository provideUsersRepository(@NonNull Context context) {
        checkNotNull(context);
        AppExecutors appExecutors = new AppExecutors();
        return UsersRepository.getInstance(UsersLocalDataSource.getInstance(appExecutors), UsersRemoteDataSource.getInstance(appExecutors),appExecutors);
    }

    public static StuffsRepository provideStuffRepository(@NonNull Context context) {
        checkNotNull(context);
        AppExecutors appExecutors = new AppExecutors();
        return StuffsRepository.getInstance(StuffsLocalDataSource.getInstance(appExecutors), StuffsRemoteDataSource.getInstance(appExecutors));
    }
}
