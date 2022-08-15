package edu.wpi.scheduler.client.storage;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;

import edu.wpi.scheduler.client.Scheduler;
import edu.wpi.scheduler.client.controller.SectionProducer;
import edu.wpi.scheduler.client.controller.StudentSchedule;
import edu.wpi.scheduler.shared.model.Course;
import edu.wpi.scheduler.shared.model.Department;
import edu.wpi.scheduler.shared.model.Section;

public class StorageStudentSchedule {

	private static boolean coursesLoaded = false;

	public static native void console(String text)
	/*-{
	    console.log(text);
	}-*/;

	public static void saveSchedule(StudentSchedule schedule) {
		if( coursesLoaded == false ){
			Window.alert("Saving courses before loading!");
		}

		JsArray<SectionProducerData> sections = JavaScriptObject.createArray().cast();

		for (SectionProducer producer : schedule.sectionProducers) {
			SectionProducerData data = JavaScriptObject.createObject().cast();
			JsArrayString arr = JavaScriptObject.createArray().cast();

			for (Section section : producer.deniedSections) {
				arr.push(section.number);
			}

			data.setDepartment(producer.getCourse().department.abbreviation);
			data.setName(producer.getCourse().number);
			data.setSections(arr);

			sections.push(data);
		}

		Storage localStorage = Storage.getLocalStorageIfSupported();

		if (localStorage != null) {
			JSONArray jsonArr = new JSONArray(sections);
			localStorage.setItem("savedCourse", jsonArr.toString());
		}
	}

	private static Course getCourse(SectionProducerData data) {
		String department = data.getDepartment();
		String courseName = data.getName();

		for (Department dept : Scheduler.getDatabase().departments) {
			if (department.equals(dept.abbreviation)) {
				for (Course course : dept.courses) {
					if (courseName.equals(course.number))
						return course;
				}
			}
		}
		return null;

	}

	public static void loadSchedule( StudentSchedule schedule ) {
		coursesLoaded = true;
		Storage localStorage = Storage.getLocalStorageIfSupported();

		if (localStorage == null)
			return;

		//try to load permutation from share code if exists
		console("trying to load share code");
		try {
			String shareCode = com.google.gwt.user.client.Window.Location.getParameter("share");
			console("share code: " + shareCode);
			loadScheduleFromParam(schedule, shareCode);
			return;
		}catch (Exception e) {
			//no share code... do nothing
			console("caught null pointer exception -- no share code");
		}

		String depList = localStorage.getItem("savedCourse");

		if (depList == null)
			return;

		schedule.sectionProducers.clear();



		try {
			JsArray<SectionProducerData> sections = JsonUtils.unsafeEval(depList).cast();

			for (int i = 0; i < sections.length(); i++) {
				SectionProducerData data = sections.get(i);
				Course course = getCourse(data);

				if (course == null)
					continue;

				SectionProducer producer = schedule.addCourse(course, null);

				if (producer == null)
					continue;

				for (Section section : course.sections) {
					if (data.hasSections(section.number))
						producer.denySection(section);
				}
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

	}

	public static void loadScheduleFromParam(StudentSchedule schedule, String shareCode) {
		
		schedule.sectionProducers.clear();
		
		
		assert (shareCode.substring(0, 2).equals("01"));


		for (int i = 2; i < shareCode.length(); i += 5) {
			int crn = Integer.parseInt(shareCode.substring(i, i + 5), 16);

			Section thisSection = Scheduler.getDatabase().getSectionByCRN(crn);

			if (thisSection != null) {
				Course course = thisSection.course;
				SectionProducer producer = schedule.addCourse(course, null);
				if(producer == null) {
					continue;
				}
				for (Section section : course.sections) {
					if(thisSection.crn != section.crn) {
						producer.denySection(section);
					}
				}
			}
		}
	}
}

class SectionProducerData extends JavaScriptObject {
	protected SectionProducerData() {
	}

	public final native void setDepartment(String dept) /*-{
		this.dept = dept;
	}-*/;

	public final native String getDepartment() /*-{
		return this.dept;
	}-*/;

	public final native void setName(String course) /*-{
		this.name = course;
	}-*/;

	public final native String getName() /*-{
		return this.name;
	}-*/;

	public final native void setSections(JsArrayString sections) /*-{
		this.sections = sections;
	}-*/;

	public final native JsArrayString getSections() /*-{
		return this.sections;
	}-*/;

	public final native boolean hasSections(String sectionName) /*-{
		return this.sections.indexOf(sectionName) != -1;
	}-*/;
}