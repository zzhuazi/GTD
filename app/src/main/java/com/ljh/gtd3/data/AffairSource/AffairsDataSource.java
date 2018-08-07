package com.ljh.gtd3.data.AffairSource;


import android.support.annotation.NonNull;

import com.ljh.gtd3.data.entity.Affair;

import java.util.List;

/**
 * Created by Administrator on 2018/3/9.
 */

public interface AffairsDataSource {

    interface GetAffairsCallBack{
        void onAffairsLoaded(List<Affair> affairs, String message);
        void onAffairsFail(String message);
    }

    interface GetAffairCallBack{
        void onAffairLoaded(Affair affair, String message);
        void onAffairFail(String message);
    }

    interface SendRequestCallBack{
        void onRequestSuccess(String message);
        void onRequestFail(String message);
    }

    void getAffair(@NonNull String affairId, @NonNull GetAffairCallBack callBack);

    void getAffairs( @NonNull String stuffId, @NonNull GetAffairsCallBack callBack);

    void addAffair(@NonNull Affair affair);

    void updateAffair(@NonNull Affair affair);

    void deleteAffair(@NonNull String affairId, @NonNull SendRequestCallBack callBack);

    void deleteAffairs(@NonNull String stuffId, @NonNull SendRequestCallBack callBack);
}
