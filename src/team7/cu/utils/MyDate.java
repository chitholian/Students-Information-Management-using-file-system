package team7.cu.utils;

public class MyDate {
    public static final int YEAR = 1, MONTH = 2, DATE = 3;
    public static final String[] monthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December"};
    private int year, month, day;

    public MyDate() {
        year = 1970;
        month = 1;
        day = 1;
    }

    public static MyDate build(String dateString) throws InvalidDateException {
        MyDate date = new MyDate();
        date.parse(dateString);
        return date;
    }

    public static MyDate copy(MyDate MyDate) {
        MyDate date = new MyDate();
        date.day = MyDate.day;
        date.month = MyDate.month;
        date.year = MyDate.year;
        return date;
    }

    public void setDate(int year, int month, int day) throws InvalidDateException {
        try {
            parse(year + "-" + month + "-" + day);
        } catch (InvalidDateException ide) {
            throw new InvalidDateException("The combination of year=" + year + ", month=" + month + ", day=" + day + ", " +
                    "does not make a valid date.");
        }
    }

    public int get(int field) {
        if (field == DATE) return day;
        else if (field == MONTH) return month;
        else if (field == YEAR) return year;
        return 0;
    }

    @Override
    public String toString() {
        String returnValue = monthNames[month - 1] + " ";
        if (day < 10) returnValue += '0';
        return returnValue + day + ", " + year;
    }

    public String toUnixFormat() {
        return year + "-" + month + "-" + day;
    }

    public void parse(String dateString) throws InvalidDateException {
        try {
            String[] tokens = dateString.split("-");
            if (tokens.length != 3) throw new Exception("Invalid DateString.");
            int y = Integer.parseInt(tokens[0]);
            int m = Integer.parseInt(tokens[1]);
            int d = Integer.parseInt(tokens[2]);
            if (!Helper.isValidDate(y, m, d)) throw new Exception("Invalid date.");
            else {
                year = y;
                month = m;
                day = d;
            }
        } catch (Exception e) {
            throw new InvalidDateException("The DateString tried to parse : \"" + dateString + "\" is invalid!");
        }
    }

    public static class InvalidDateException extends Exception {
        public InvalidDateException(String message) {
            super(message);
        }

        public InvalidDateException() {
            super();
        }
    }
}
