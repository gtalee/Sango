package pip.util;

public class CommonFileFilter extends javax.swing.filechooser.FileFilter {
    private String[] filters;
    private String description;

    public CommonFileFilter(String filter, String desc) {
        filters = filter.split(";");
        description = desc;
    }

    public boolean accept(java.io.File f) {
        if (f.isDirectory()) {
            return true;
        }
        String name = f.getName().toLowerCase();
        for (int i = 0; i < filters.length; i++) {
            if (name.endsWith(filters[i])) {
                return true;
            }
        }
        return false;
    }

    public String getDescription() {
        return description;
    }
}
