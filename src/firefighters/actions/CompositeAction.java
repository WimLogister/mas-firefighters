package firefighters.actions;

import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompositeAction implements AbstractAction {

	@NonNull private List<PrimitiveAction> actionSequence;
	
	@Override
	public void execute() {
		actionSequence.remove(0).execute();
	}

	@Override
	public boolean checkPreconditions() {
		return actionSequence.get(0).checkPreconditions();
	}
}
