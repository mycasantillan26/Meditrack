    package com.example.meditrack;
    import android.content.Context;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.BaseAdapter;
    import android.widget.TextView;
    import java.text.SimpleDateFormat;
    import java.util.Calendar;
    import java.util.Date;
    import java.util.HashSet;
    import java.util.List;
    import java.util.Locale;
    import java.text.ParseException;


    public class CalendarAdapter extends BaseAdapter {
        private Context context;
        private List<Date> monthDates;
        private HashSet<Date> eventDates;
        private LayoutInflater inflater;
        private SimpleDateFormat dateFormatter;

        public CalendarAdapter(Context context, List<Date> monthDates, HashSet<Date> eventDates) {
            this.context = context;
            this.monthDates = monthDates;
            this.eventDates = eventDates;
            this.inflater = LayoutInflater.from(context);
            this.dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Used for date comparison
        }

        @Override
        public int getCount() {
            return monthDates.size();
        }

        @Override
        public Object getItem(int position) {
            return monthDates.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Date date = monthDates.get(position);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.calendar_day_layout, parent, false);
            }
            TextView dayNumber = convertView.findViewById(R.id.dayNumber);
            View dotView = convertView.findViewById(R.id.dotView);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date normalizedDate = sdf.parse(sdf.format(date));
                dayNumber.setText(String.format(Locale.getDefault(), "%td", normalizedDate));
                if (eventDates.contains(normalizedDate)) {
                    dotView.setVisibility(View.VISIBLE);
                } else {
                    dotView.setVisibility(View.INVISIBLE);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                dotView.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }
    }
