package ru.unn.agile.MarksAccounting.Model;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class TableOfMarks {

    private final ArrayList<Group> groups;

    private int findGroup(final String requiredGroup) {
        int i = 0;
        while (i < getGroups().size()
                && getGroups().get(i).getNumber().compareTo(requiredGroup) <= 0) {
            if (getGroups().get(i).getNumber().equals(requiredGroup)) {
                return i;
            }
            i++;
        }
        throw new GroupDoesNotExistException("Required group doesn't exist");
    }

    public TableOfMarks() {
        groups = new ArrayList<Group>();
    }

    public ArrayList<Group> getGroups() {
        return groups;
    }

    public void addGroup(final String newGroup) {
        int i = 0;
        while (i < getGroups().size()
                && getGroups().get(i).getNumber().compareTo(newGroup) <= 0) {
            if (getGroups().get(i).getNumber().equals(newGroup)) {
                i = -1;
                break;
            }
            i++;
        }
        if (i != -1) {
            this.groups.add(i, new Group(newGroup));
        }
    }

    public void addStudent(final String requiredGroup, final String newStudent)
    {
        this.groups.get(findGroup(requiredGroup)).addStudent(newStudent);
    }

    public void addAcademicSubject(final String requiredGroup, final String newAcademicSubject)
    {
        this.groups.get(findGroup(requiredGroup)).addAcademicSubject(newAcademicSubject);
    }

    public void addNewMark(final Mark newMark, final String requiredStudent,
                           final String requiredGroup) {
        this.groups.get(findGroup(requiredGroup)).addNewMark(newMark, requiredStudent);
    }

    @Override
    public int hashCode() {
        return this.groups.hashCode();
    }

    @Override
    public boolean equals(final Object comparedTableOfMarks) {
        TableOfMarks temp = (TableOfMarks) comparedTableOfMarks;
        return this.groups.equals(temp.getGroups());
    }

    public void deleteGroup(final String requiredGroup) {
        this.groups.remove(findGroup(requiredGroup));
    }

    public int getMark(final String requiredGroup, final String requiredStudent,
                       final String requiredAcademicSubject, final GregorianCalendar requiredDate) {
        return getGroups().get(findGroup(requiredGroup)).getMark(requiredStudent,
                requiredAcademicSubject, requiredDate);
    }

    public void deleteMark(final String requiredGroup, final String requiredStudent,
                            final String requiredAcademicSubject,
                            final GregorianCalendar requiredDate) {
        this.groups.get(findGroup(requiredGroup)).deleteMark(requiredStudent,
                requiredAcademicSubject, requiredDate);
    }

    public void deleteAcademicSubject(final String requiredGroup,
                                      final String requiredAcademicSubject) {
        this.groups.get(findGroup(requiredGroup)).deleteAcademicSubject(requiredAcademicSubject);
    }

    public void deleteStudent(final String requiredGroup, final String requiredStudent) {
        this.groups.get(findGroup(requiredGroup)).deleteStudent(requiredStudent);
    }
}
