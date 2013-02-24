package edu.wpi.scheduler.client.permutation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

import edu.wpi.scheduler.client.controller.ProducerUpdateEvent;
import edu.wpi.scheduler.client.controller.ProducerUpdateEvent.UpdateType;
import edu.wpi.scheduler.client.controller.ScheduleConflictController;
import edu.wpi.scheduler.client.controller.SchedulePermutation;
import edu.wpi.scheduler.client.controller.ScheduleProducer;
import edu.wpi.scheduler.client.controller.ScheduleProducer.ProducerEventHandler;
import edu.wpi.scheduler.client.controller.SectionProducer;
import edu.wpi.scheduler.client.controller.StudentSchedule;
import edu.wpi.scheduler.client.controller.StudentScheduleEvent;
import edu.wpi.scheduler.client.controller.StudentScheduleEventHandler;
import edu.wpi.scheduler.client.permutation.view.PeriodDescriptionDialogBox;
import edu.wpi.scheduler.shared.model.Course;
import edu.wpi.scheduler.shared.model.DayOfWeek;
import edu.wpi.scheduler.shared.model.Period;
import edu.wpi.scheduler.shared.model.Section;
import edu.wpi.scheduler.shared.model.Term;

public class PermutationController implements HasHandlers, StudentScheduleEventHandler {

	private HandlerManager handlerManager = new HandlerManager(this);

	public final StudentSchedule studentSchedule;

	protected double startTime = 8.0;
	protected double endTime = 16.0;

	protected List<DayOfWeek> validDayOfWeek =
			Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);

	/**
	 * Current selected schedule to be displayed
	 */
	protected SchedulePermutation selectedPermutation;

	/**
	 * To quickly display the section times on the course view
	 */
	protected Section selectedSection = null;

	/**
	 * Find out conflicts between schedules
	 */
	public final ScheduleConflictController conflictController = new ScheduleConflictController();

	/**
	 * Generate schedules from the conflicts
	 */
	private ScheduleProducer producer;

	public final Map<Term, String> termColor = new HashMap<Term, String>();

	public final String[] colors = new String[] {
			"rgb(172, 114, 94)", "rgb(250, 87, 60)", "rgb(255, 173, 70)",
			"rgb(66, 214, 146)", "rgb(123, 209, 72)", "rgb(154, 156, 255)",
			"rgb(179, 220, 108)", "rgb(255, 173, 70)", "rgb(202, 189, 191)",
			"rgb(251, 233, 131)", "rgb(205, 116, 230)", "rgb(194, 194, 194)",
			"rgb(159, 225, 231)", "rgb(246, 145, 178)", "#92E1C0",
			"rgb(251, 233, 131)", "#7BD148", "rgb(159, 198, 231)" };

	public PermutationController(StudentSchedule studentSchedule) {
		this.studentSchedule = studentSchedule;
		updateProducer();

		studentSchedule.addStudentScheduleHandler(this);
		updateTimeRange();
		
		for (SectionProducer producer : studentSchedule.sectionProducers) {
			conflictController.addCourse(producer.getCourse());
		}
	}

	public String getCourseColor(Course course) {

		for (int i = 0; i < studentSchedule.sectionProducers.size() && i < colors.length; i++) {
			SectionProducer producer = studentSchedule.sectionProducers.get(i);
			if (producer.getCourse().equals(course)) {
				return colors[i];
			}
		}

		return "rgb(255,255,255)";
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		handlerManager.fireEvent(event);
	}

	public StudentSchedule getStudentSchedule() {
		return studentSchedule;
	}

	public HandlerRegistration addTimeChangeListner(TimeRangeChangEventHandler handler) {
		return handlerManager.addHandler(TimeRangeChangeEvent.TYPE, handler);
	}

	public void removeTimeChangeListner(TimeRangeChangEventHandler handler) {
		handlerManager.removeHandler(TimeRangeChangeEvent.TYPE, handler);
	}

	public HandlerRegistration addSelectListner(PermutationSelectEventHandler handler) {
		return handlerManager.addHandler(PermutationSelectEvent.TYPE, handler);
	}

	public void removeSelectListner(PermutationSelectEventHandler handler) {
		handlerManager.removeHandler(PermutationSelectEvent.TYPE, handler);
	}

	/**
	 * For events related to the schedule producer
	 * 
	 * @param handler
	 * @return
	 */
	public HandlerRegistration addProduceHandler(ProducerEventHandler handler) {
		return handlerManager.addHandler(ProducerUpdateEvent.TYPE, handler);
	}

	public void removeProduceHandler(ProducerEventHandler handler) {
		handlerManager.removeHandler(ProducerUpdateEvent.TYPE, handler);
	}

	public double getStartHour() {
		return startTime;
	}

	public double getEndHour() {
		return endTime;
	}

	public List<DayOfWeek> getValidDaysOfWeek() {
		return validDayOfWeek;
	}

	public void setTimeRange(double startTime, double endTime) {

		startTime = Math.floor(startTime);
		endTime = Math.ceil(endTime);

		if (this.startTime == startTime && this.endTime == endTime)
			return;

		this.startTime = startTime;
		this.endTime = endTime;

		this.fireEvent(new TimeRangeChangeEvent());
	}

	private void updateTimeRange() {
		double startTime = 10.0;
		double endTime = 16.0;

		for (SectionProducer producer : studentSchedule.sectionProducers) {
			for (Section section : producer.getCourse().sections) {
				for (Period period : section.periods) {
					startTime = Math.min(period.startTime.getValue(), startTime);
					endTime = Math.max(period.endTime.getValue(), endTime);
				}
			}
		}

		setTimeRange(startTime, endTime);
	}

	public void selectPermutation(SchedulePermutation permutation) {

		if (selectedPermutation != null && selectedPermutation.equals(permutation))
			return;

		this.selectedPermutation = permutation;

		this.fireEvent(new PermutationSelectEvent());
	}

	public SchedulePermutation getSelectedPermutation() {
		return selectedPermutation;
	}

	public Section getSelectedSection() {
		return this.selectedSection;
	}

	public void setSelectedSection(Section section) {
		if (this.selectedSection != null && this.selectedSection.equals(section))
			return;
		
		this.selectedSection = section;
		
		this.fireEvent(new PermutationSelectEvent());
	}

	@Override
	public void onCoursesChanged(StudentScheduleEvent studentScheduleEvent) {
		for (SectionProducer producer : studentSchedule.sectionProducers) {
			conflictController.addCourse(producer.getCourse());
		}

		updateProducer();
		updateTimeRange();
	}

	private void updateProducer() {
		if (producer != null) {
			producer.cancel();
		}
		
		producer = new ScheduleProducer(this);
		fireEvent(new ProducerUpdateEvent(UpdateType.NEW));
		
		producer.start();
		
		if( producer.getPermutations().size() == 0 )
			selectPermutation(null);
	}

	public ScheduleProducer getProducer() {
		return producer;
	}

	public void displayDescription(Section section) {
		PeriodDescriptionDialogBox dialog = new PeriodDescriptionDialogBox(this, section);
		dialog.setGlassEnabled(true);
		dialog.center();
		dialog.show();
	}
}
