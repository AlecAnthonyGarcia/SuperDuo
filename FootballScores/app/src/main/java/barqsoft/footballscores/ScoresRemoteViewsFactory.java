package barqsoft.footballscores;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ScoresRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private Cursor mCursor;
    private int mAppWidgetId;

    public ScoresRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        mCursor = mContext.getContentResolver().query(
                DatabaseContract.scores_table.buildScoreWithDate(),
                null, null, new String[]{format.format(date)}, null);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews row = new RemoteViews(mContext.getPackageName(),
                R.layout.scores_list_item);

        mCursor.moveToPosition(position);

        row.setTextViewText(R.id.home_name, mCursor.getString(scoresAdapter.COL_HOME));
        row.setTextViewText(R.id.away_name, mCursor.getString(scoresAdapter.COL_AWAY));
        row.setTextViewText(R.id.data_textview, mCursor.getString(scoresAdapter.COL_MATCHTIME));
        row.setTextViewText(R.id.score_textview, Utilies.getScores(mCursor.getInt(scoresAdapter.COL_HOME_GOALS),
                mCursor.getInt(scoresAdapter.COL_AWAY_GOALS)));
        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}