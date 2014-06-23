package rf.demo.uberimagesearch;

import java.util.List;

import rf.demo.uberimagesearch.api.WebImage;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Simple Adapter for Image URLs.
 * Images are loaded using Picasso.
 */
public class ImagesAdapter extends ArrayAdapter<WebImage> {
	Activity activity;
	
	public ImagesAdapter(Activity activity, List<WebImage> images) {
		super(activity, 0, images);
		this.activity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImgViewHolder viewHolder = null;
		if (convertView==null || convertView.getTag() == null) {
			convertView = activity.getLayoutInflater().inflate(R.layout.img_cell, parent, false);

			viewHolder = new ImgViewHolder(convertView);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ImgViewHolder) convertView.getTag();

		}
		
		viewHolder.refresh(getItem(position));
		
		return convertView;
	}
	
	
	/**
	 * Save the views to optimize recycling
	 */
	class ImgViewHolder {
		
		final ImageView imageView;
		final TextView errorTextView;
		
		public ImgViewHolder(View cell) {
			imageView = (ImageView) cell.findViewById(R.id.img_row);
			errorTextView = (TextView) cell.findViewById(R.id.img_row_message);
		}

		public void refresh(WebImage webImage) {
			errorTextView.setVisibility(View.GONE);
			
			Picasso.with(activity)
				.load(webImage.tbUrl)
				.into(imageView, new Callback() {
					
					@Override
					public void onSuccess() { }
					
					@Override
					public void onError() {
						errorTextView.setVisibility(View.VISIBLE);
					}
				});

		}
		
	}
	
}
