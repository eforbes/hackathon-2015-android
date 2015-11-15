package michrosoft.com.hackathon2015;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by davidalbers on 11/14/15.
 */
public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.ViewHolder> {
    private OnInvitationAccepted mainCheckedChanged;
    private OnInvitationClickedEvent mainInvitationClicked;

    private ArrayList<Invitation> invitations;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        @Bind(R.id.about_invitation) TextView aboutInvitation;
        @Bind(R.id.accept_invitation) Switch acceptInvitation;
        @Bind(R.id.invitation_time_start) TextView timeStartText;
        @Bind(R.id.invitation_time_left) TextView timeLeftText;
        View view;
        public ViewHolder(View v) {
            super(v);
            view = v;
            ButterKnife.bind(this, v);
        }
    }

    public InvitationAdapter(ArrayList<Invitation> invitations, OnInvitationAccepted invitationCheckedChanged, OnInvitationClickedEvent invitationClickedEvent) {
        this.mainCheckedChanged = invitationCheckedChanged;
        this.invitations = invitations;
        this.mainInvitationClicked = invitationClickedEvent;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public InvitationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invitation_card_layout, parent, false);


        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.aboutInvitation.setText(invitations.get(position).getDescription());
        holder.timeStartText.setText(invitations.get(position).getStart_time());
        holder.timeLeftText.setText(invitations.get(position).getResponse_deadline());
        if(invitations.get(position).getStatus() == 1)
            holder.acceptInvitation.setChecked(true);
        else
            holder.acceptInvitation.setChecked(false);

        String parsedTime = invitations.get(position).getStart_time();




        holder.acceptInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainCheckedChanged.onCheckedChanged(invitations.get(position), holder.acceptInvitation, position);
            }
        });
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainInvitationClicked.onInvitationClicked(invitations.get(position));
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return invitations.size();
    }
}
