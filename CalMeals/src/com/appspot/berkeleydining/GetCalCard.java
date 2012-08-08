	/**
	 * Facilitates the various HTTP POST requests that are needed 
	 * to retrieve student balances. 
	 * 
	 * @author Jeff Butterfield and Shouvik Dutta
	 * 
	 */

package com.appspot.berkeleydining;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class GetCalCard {

	/**Returns a pair of Meal Point and Debit balances after performing several HTTP
	 * operations.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public String[] getInfo(String username, String password) {
		HttpClient client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		HttpClientParams.setRedirecting(params, true);
		HttpPost post = new HttpPost("https://auth.berkeley.edu/cas/login?service=https://services.housing.berkeley.edu/c1c/dyn/CasLogin.asp");
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("username", username));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs
					.add(new BasicNameValuePair(
							"lt",
							"_cNoOpConversation id_krO0ABXNyAGFvcmcuc3ByaW5nZnJhbWV3b3JrLndlYmZsb3cuZXhlY3V0aW9uLnJlcG9zaXRvcnkuY29udGludWF0aW9uLlNlcmlhbGl6ZWRGbG93RXhlY3V0aW9uQ29udGludWF0aW9uPMZQHZQEyycMAAB4cgBXb3JnLnNwcmluZ2ZyYW1ld29yay53ZWJmbG93LmV4ZWN1dGlvbi5yZXBvc2l0b3J5LmNvbnRpbnVhdGlvbi5GbG93RXhlY3V0aW9uQ29udGludWF0aW9ujvgpwtO1430CAAB4cHoAAAQAAAAJ5x-LCAAAAAAAAAClWX1sHEcVH_viJHbi5BzHzldTQigkceg5bvPR9EKKc7aTC-c0xGka6tJmvDu-23hvZzM7Z19AbfNPqko0QENSPgQEVDUVUiQKf0CV_hFCCfxREkFFpFJUqRICCVUgAUJISAXem539uLu1z1FPymTnzczvvXnz3ps3z1f-Sto8QXZzUcx4rrCc4qSgZTbDxVRmhk1M2nwmw5yi5bCMVXbtzAgQhqvMqEiLO3mgrH6gy3v_8vVzSwmpupJ02hwm36uXAvLKk3SaZmC6nSlYzhQzC5Ynl24Z-_zBE89tTOGimQWEkBaYuutOhBhjnqdFKG688pb55NZbSoRT5GnSCnJMW2ymgLKMcFFuCh7sKA49JqmseBM3Pjvz55fe29YK4IJsS0IxuGAgX6XsZXCNZRToBLOZOQykaz_uvHQwW3yrlbQUyAKDm0ySrgLqpN-mTrF_rMSFzALyfU2QByc8KaghY9jPm0f3P_6dV_-NormwxeVK1QibUbCl0V3d20-8cwR4j5G2aWpXGDBKR7MOVcoTTDx75eL6JRfe-5KCISkA-tRculIyGdy2maE0VuAGtQelFNZERbJR6m7eW_pC-vipZ1K45Q4ajHiSLPc3jsbQDxOzSuquyEAOUK8E9LZF71x_o_fEb1OkdYR02JyaI7BxLvKkXZYE80rcNqvuQ58m-Fs6sxjaNPxrk2SRx8S0ZTAwxCWGYCZzpEVtD7jswy2dpJ5VzBjUy9CKLOGgQdUecKuG5VI78wggOLDlw9TzYNtmLkJ5fvtLbRv6zv2klbQWyGJXT5BkRfw4JSotC-MVDYT2uLrquiBSl1ERAsDQIh-eOAn6w8E1oJcZKpwc51MWO4anFGpFndI-zm1GnZsbxJnb3_7P3-A0HwtO0yWS9MIuppjcLyiI6RSPql7edKtVD-HTqu1u0Bb4yN6kYwZgy_S1ss9yzOEqahN7DcKDlJ-8E4S7br59-eqaV7cqV-icgCFYdoR5FVtKAmYkiv11UP0RVP---Hx0mO5IQyGL33zwkb1rs3f9wnfW2IyjJcFn6ITNbv9yx6bdM6-9mSKpAmkzKByTJD2xEwxnwiF2grdSyx6FeECL_kmOkw5PUmPqKPgirLx7vObwg4Fhm5VBV1k_HG12KyKYqd2zYWbrSN-ePR9s3O07ISF7QLvr5lhA22986xM73z3bShbkwUcgNPrOXCDLTGbYFPWYs8FGldRgj5OWzQ4F9gh-WWayxM1DkYVCDO6UZEuTEx2ikuJRMAFmG3UyKKok7UUmh4Xgwue7FczsaUnunSucUD-KoFkNqk_AjToad5nlWMry6sAv4McANvdJsoo5XkWwaNpw1eUeM3oAAAQAg9ktf4rPboeDr7jqclDj_4OfJB_3Kk5GsEkMbpn9zGGCSmaOKl0NGgYYAhfbd4EvL7ScaT7FAmz04s3xtUMQpIsUHbJ2Md5Xknx0rmG1Z8R9MNznI5Ksjowh4OEvhVDnfzSuI4YkO-dSvoq6Q5bnUmmUfJS82hec7tpEegKTs-C98zjhUfBbKzjidKwX2I7J_cQiUurPJBmYB3JwNQbg3bUEjb-I1aGfl8mXbl3GMeg4XKIVBPAr6yihSj4TQmebCB5Aq_X-ruG-BsFrCAnA32ziTTXAmGmBN0WdBMAnZHIQrwM8BtkU5jYQ8paF34Fi4eAcyUSIeQ18YR6YGq8jjtXGapB-N7_tQmB0PEsHj6iTsN2dMjmFqwMcYoYVpIAg4oqafgh7PNoxfjyGzTg2j0cbwI8nsHmyThD8UCgTHxZqqomTNygJbze9s9UJ1OBYwRCn4TjCINQryaZ58MEMGrwZ_ws1NRUKe1OSwfmm-UfYKciFZA6OVXAb_2NV6QfQu2cd09IvgUzPoXbNDsj3m0SqGu_RGkrHendw8q3t-FHGBoyyDXIGEclxHT9UanAKm2o48CP5IV5ikAo20EKJIx6vS7Kj-VsIok8EFlxbPfUkreyFNq04Rilk8ZQkD82LhVdxXQh8ipU-0QPUMW28f1Y1EjW7rpLqxsZDzi_L-Tz0gHN52lBctQEpjt21BM2tx-emOeUxPoFhxSPybN6XwdeIzaRiFtxJcYarGomaaWcN05DZNuk_ZJoxG0MLYRGq1t-gSV2JjD8294TgWH0hAu6pqdlvtZB7kDQwMeZTwCobaBq_w-TBUMhjE36cx-ZrKiUY8x90oVtlZw_goQwjATUUoaeeFOSVruCYeNXpueVf-PENbOB420y-n0Vh0ILog6urIb-SlG7mADQhv3SsF7pgTwhxEz--i82lmgHi6ds4eqjivjD5zYzRSZag3LWJ9EaeLRsl6Udo6lKYCNgSknqH-s_5Qde19Vt4xLLBAnIlakHcWpc8oLWXtrQzDHF_MNzHLfx4GZvLkiw264fBjO9HWSzHc9mUFCBEuQz3NW42Z1sQsvPOJIeXGKOmKi_46yXZMMdoIFTArcFTC5prkulM-hAPOwY7zIQ2h4DrmiRyqOIfhAzej3aNzStJ6qhd8Red3yQeClw5jkmF-aigrsvEMWpPw320JomckJVfmw-0vjfroePkhNfI_VqR9dCxogpE2MF4bx_Fd3ZvAy1B8Gck6Wsq-AHuBVL3NtASQCcl2ZoEOo3zvYx6Lx5heBdp1J56UoIeturn8iyg_puuwIsasruWkCBlWbv_nFsfVjmAxlydQG0EbumbZfsGdxyGpbVMjp_mkoUXxIqafmM4SXoAAAHsfU7n-AGimq4i4cCAioUDA4f92IoPm5V1lCDF1OE31GhBJw5zo0pucPsev5vzdwDxKUwcumsnhsLfCtVR53aSl0Ej_mPYgeB5MA8PGtPlEN7ueRQiBIKmY0QtfUpUnFDyF2W8sOoHJ8hC_Y9QhNtVT5AHmhXNGHVAePBtebqm6vXKCxfXfXnzm79WBbSFkFEW8QaIlx_9whxWx_Y0YRJkHomMjI71v7o60vdFv4y2fZ5QNRDyxlfe3nT-3dutZAHIylQ9BlQUKwDjgzRbICvLfnUtx00G97DHwYJB3zua1QNHE5YBXAdXKohVutKYZAqYzcwRi9lmXR16jCl9JT4zEzbpV5aOvrDwe13_6LygSsEdDlwKzDxMZUmzXB5RVOEuLPn7HJGUxT-WkLBor2xPDaT_2_fz19ufXe_rPh2NHlOO-vuvPzV-JvvDlhRJ5UmXgRZsydN5xxCqLJgnS5lfIMzxiiPHyRLdxVIduMZ4grG4RP8qIpjgW7Ge8NVbxy-lvS12UJrscMOf_6pIqb_gELTtB5uVD9kkBetIOrzOM3_45xvncn_3rRtObNKqKn36RdQ_YtPb-PcCOL-rQ2cvvvjaT7f7f01aigVuXe4mCmGNbO5ztaXuqOiP6zdXq261Wv0_gRZREisbAAABeA.."));
			nameValuePairs.add(new BasicNameValuePair("_eventId", "submit"));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(post);
			response.getEntity().consumeContent();
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				throw new IOException(response.getStatusLine().toString());
			String line = "";
			HttpGet get = new HttpGet(
					"https://services.housing.berkeley.edu/c1c/dyn/balance.asp");
			HttpContext context = new BasicHttpContext();
			HttpResponse response2 = client.execute(get, context);
			BufferedReader rd2 = new BufferedReader(new InputStreamReader(
					response2.getEntity().getContent()));
			response2.getEntity().consumeContent();
			post = new HttpPost(
					"https://services.housing.berkeley.edu/c1c/dyn/login.asp");
			nameValuePairs.clear();
			nameValuePairs.add(new BasicNameValuePair("submit1", "Continue"));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = client.execute(post);
			response.getEntity().consumeContent();
			response2 = client.execute(get, context);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
				throw new IOException(response.getStatusLine().toString());
			rd2 = new BufferedReader(new InputStreamReader(response2
					.getEntity().getContent()));
			String[] resultPair = new String[2];
			resultPair[0] = "0.00";
			resultPair[1] = "0.00";
			while ((line = rd2.readLine()) != null) {
				if (line.contains("Cal Dining Resident Plans:")) {
					String[] mpComp = line.split("Cal Dining Resident Plans:");
					String[] mpComp2 = mpComp[1].split("<b>|  Total");
					resultPair[1] = mpComp2[1];
					break;
				}
				if (line.contains("Debit:")) {
					
					String[] debitComp = rd2.readLine().split("<b>|</b>");
					resultPair[0] = debitComp[1];
					continue;
				}
				if (line.contains("Plans:")) {
					
					String[] debitComp = rd2.readLine().split("<b>|</b>");
					resultPair[1] = debitComp[1];
					break;
				}
			}
			return resultPair;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
