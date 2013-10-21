package scoring;


import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import utility.DownloadResultEntryContent;
import core.Result;
import core.StaticData;


public class ResultScoreManager {

	/**
	 * @param args
	 */
	//score calculators
	ResultTitleMatcher titleMatcher;
	ResultStackTraceMatcher stackTraceMatcher;
	SourceCodeContextMatcher sourcecodeContextMatcher;
	AlexaCompeteScore alexaCompeteScorer;
	SOVoteScore so_vote_Score;
	SEConfidenceScore se_confidence_score;
	DownloadResultEntryContent downloader;
	public ArrayList<Result> Final_Results;
	
	//other variables
	String stackTrace;
	String queryTitle;
	String code_context;
	HashMap<String, Double> recentPageScores;

	//default constructor
	public ResultScoreManager(ArrayList<Result> myResults)
	{
		this.Final_Results=myResults;
	}
	
	//custom constructor
	public ResultScoreManager(ArrayList<Result> myResults, String queryTitle,
			String currentExceptionMessage, String stackTrace,
			String code_context) {
		// initiating the results
		this.Final_Results = myResults;
	}
	
	public void calculate_relative_scores()
	{
		try {
			// ALEXA COMPETE rank score
			alexaCompeteScorer = new AlexaCompeteScore(this.Final_Results);
			this.Final_Results = alexaCompeteScorer
					.get_alexa_compete_relative_rank_score();
			System.out.println("Alexa Compete score finalized.");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		try {
			// SO vote score
			so_vote_Score = new SOVoteScore(this.Final_Results);
			this.Final_Results = so_vote_Score.get_SO_relative_score();
			System.out.println("SO Vote score finalized.");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		try
		{
			//SE confidence
			se_confidence_score=new SEConfidenceScore(this.Final_Results);
			this.Final_Results=se_confidence_score.get_normalized_confidence();
			System.out.println("SE Confidence score normalized.");
		}catch(Exception exc){
			exc.printStackTrace();
		}
	}
	
	
	public ArrayList<Result> prerpare_final_score()
	{
		
		//different heuristic scores
		double page_title_weight=0.5;
		double code_stack_weight=0.3;
		double page_body_weight=0.2;
		double stack_trace_weight=0.6;
		double code_context_weight=0.4;
		/*if(this.code_context.isEmpty() || this.code_context==null)
		{
			code_context_weight=0;
			stack_trace_weight=1.0;
		}*/
		double recent_history_weight=0.0; //discarded for the purpose of testing
		double alexa_rank_weight=0.3;
		double so_vote_weight=0.7;
		
		//accepted weights
		double content_weight=0.35;
		double context_weight=0.85;
		double popularity_weight=0.20;
		double result_confidence_weight=0.10;
		
		//code preparing the final score
		for(Result result:this.Final_Results)
		{	
			//double popularity_score=0;
			//double content_score=0;
			//double context_score=0;
			//double se_recommendation_score=0;
			try
			{
				//content
				result.content_score=(result.title_title_MatchScore*page_title_weight+
						result.title_codestack_MathScore*code_stack_weight+result.title_content_MatchScore*page_body_weight); //title 2 body deleted
				//context
				result.context_score=(result.stackTraceMatchScore*stack_trace_weight+
						result.sourceContextMatchScore*code_context_weight+result.recentHistoryScore*recent_history_weight); //recency score will be added
				//popularity
				result.popularity_score=(result.SOVoteScore*so_vote_weight+result.AlexaCompeteRankScore*alexa_rank_weight);
				
				//result confidence
				//result_confidence=result.search_result_confidence>=1?1:result.search_result_confidence;
				//result.search_result_confidence=1.0;// result_confidence;
				//result.search_result_confidence=result_confidence*result.search_result_confidence;
				
				//different total scores
				result.totalScore_content_context=content_weight*result.content_score+ context_weight*result.context_score;
				result.totalScore_content_popularity=content_weight*result.content_score+popularity_weight*result.popularity_score;
				result.totalScore_context_popularity=context_weight*result.context_score+popularity_weight*result.popularity_score; 
				
				
				result.totalScore_content_context_popularity=result.content_score*content_weight+result.context_score*context_weight
				+
				result.popularity_score*popularity_weight+result.search_result_confidence*result_confidence_weight;
			}catch(Exception exc)
			{
				exc.printStackTrace();
			}
		}
		
		//returning the result
		return Final_Results;
	}
	
	
	public ArrayList<Result> sort_the_result_ct()
	{
		//code for sorting the result
		Collections.sort(this.Final_Results, new CustomComparator_ct());
		return this.Final_Results;
	}
	
	public ArrayList<Result> sort_the_result_cx()
	{
		//code for sorting the result
		Collections.sort(this.Final_Results, new CustomComparator_cx());
		return this.Final_Results;
	}
	
	
	public ArrayList<Result> sort_the_result_ctx()
	{
		//code for sorting the result
		Collections.sort(this.Final_Results, new CustomComparator_ctx());
		return this.Final_Results;
	}
	
	public ArrayList<Result> sort_the_result_ctp()
	{
		//code for sorting the result
		Collections.sort(this.Final_Results, new CustomComparator_ctp());
		return this.Final_Results;
	}
	
	public ArrayList<Result> sort_the_result_cxp()
	{
		//code for sorting the result
		Collections.sort(this.Final_Results, new CustomComparator_cxp());
		return this.Final_Results;
	}
	
	
	public ArrayList<Result> sort_the_result_ctxp()
	{
		//code for sorting the result
		Collections.sort(this.Final_Results, new CustomComparator_ctxp());
		return this.Final_Results;
	}
	
	
	public class CustomComparator_ct implements Comparator<Result> {
	    @Override
	    public int compare(Result o1, Result o2) {
	    	if(o1.content_score>o2.content_score)return -1;
	    	else if(o1.content_score<o2.content_score)return 1;
	    	else return 0;
	    }
	}
	
	public class CustomComparator_cx implements Comparator<Result> {
	    @Override
	    public int compare(Result o1, Result o2) {
	    	if(o1.context_score>o2.context_score)return -1;
	    	else if(o1.context_score<o2.context_score)return 1;
	    	else return 0;
	    }
	}
	
	public class CustomComparator_ctx implements Comparator<Result> {
	    @Override
	    public int compare(Result o1, Result o2) {
	    	if(o1.totalScore_content_context>o2.totalScore_content_context)return -1;
	    	else if(o1.totalScore_content_context<o2.totalScore_content_context)return 1;
	    	else return 0;
	    }
	}
	
	public class CustomComparator_ctp implements Comparator<Result> {
	    @Override
	    public int compare(Result o1, Result o2) {
	    	if(o1.totalScore_content_popularity>o2.totalScore_content_popularity)return -1;
	    	else if(o1.totalScore_content_popularity<o2.totalScore_content_popularity)return 1;
	    	else return 0;
	    }
	}
	
	public class CustomComparator_cxp implements Comparator<Result> {
	    @Override
	    public int compare(Result o1, Result o2) {
	    	if(o1.totalScore_context_popularity>o2.totalScore_context_popularity)return -1;
	    	else if(o1.totalScore_context_popularity<o2.totalScore_context_popularity)return 1;
	    	else return 0;
	    }
	}
	
	
	public class CustomComparator_ctxp implements Comparator<Result> {
	    @Override
	    public int compare(Result o1, Result o2) {
	    	if(o1.totalScore_content_context_popularity>o2.totalScore_content_context_popularity)return -1;
	    	else if(o1.totalScore_content_context_popularity<o2.totalScore_content_context_popularity)return 1;
	    	else return 0;
	    }
	}
	
	
	protected double format_the_double(double unformatted)
	{
		//code for formatting the double number
		double formattedNumber=0;
		try
		{
			formattedNumber = Double.parseDouble(new DecimalFormat("#.####").format(unformatted));
		}catch(Exception exc){}
		return formattedNumber;
	}
	
	//saving content score
	public void save_the_result_score_ct(String fileName)
	{
		// code for saving the component scores
		/*try {
			//String baseFolder="D:/My MSc/CMPT 811/SurfClipse Tool Demo/data/results/ct";
			String baseFolder=StaticData.Base_Directory+"/results/ct";
			FileWriter writer = new FileWriter(new File(baseFolder+"/"+fileName));
			String header = "TotalScore\tconten\tcontext\tserecom\tpopularity\tURL \n";
			writer.write(header);
			int count = 0;
			for (Result result : this.Final_Results) {
				String line = format_the_double(result.totalScore_content) + "";
				line += "\t" + format_the_double(result.content_score);
				line += "\t" + format_the_double(result.context_score);
				line += "\t" + format_the_double(result.serecom_score);
				line += "\t" + format_the_double(result.popularity_score);
				line += "\t" + result.resultURL + "\n";
				writer.write(line);
				count++;
				// if(count==20)break;
			}
			writer.close();
			System.out.println("Scores (ct) saved successfully");
		} catch (Exception exc) {
		exc.printStackTrace();
		}*/
	}
	
	//saving content score
	public void save_the_result_score_se(String fileName)
		{
			// code for saving the component scores
			/*try {
				//String baseFolder="D:/My MSc/CMPT 811/SurfClipse Tool Demo/data/results/se";
				String baseFolder=StaticData.Base_Directory+"/results/ct";
				FileWriter writer = new FileWriter(new File(baseFolder+"/"+fileName));
				String header = "TotalScore\tconten\tcontext\tserecom\tpopularity\tURL \n";
				writer.write(header);
				int count = 0;
				for (Result result : this.Final_Results) {
					String line = format_the_double(result.serecom_score) + "";
					line += "\t" + format_the_double(result.content_score);
					line += "\t" + format_the_double(result.context_score);
					line += "\t" + format_the_double(result.serecom_score);
					line += "\t" + format_the_double(result.popularity_score);
					line += "\t" + result.resultURL + "\n";
					writer.write(line);
					count++;
					// if(count==20)break;
				}
				writer.close();
				System.out.println("Scores (se) saved successfully");
			} catch (Exception exc) {
			exc.printStackTrace();
			}*/
		}
	
	//saving content_context score
	public void save_the_result_score_ctx(String fileName)
	{
		// code for saving the component scores
		/*try {
			//String baseFolder="D:/My MSc/CMPT 811/SurfClipse Tool Demo/data/results/ctx";
			String baseFolder=StaticData.Base_Directory+"/results/ctx";
			FileWriter writer = new FileWriter(new File(baseFolder+"/"+fileName));
			String header = "TotalScore\tconten\tcontext\tserecom\tpopularity\tURL \n";
			writer.write(header);
			int count = 0;
			for (Result result : this.Final_Results) {
				String line = format_the_double(result.totalScore_content_context) + "";
				line += "\t" + format_the_double(result.content_score);
				line += "\t" + format_the_double(result.context_score);
				line += "\t" + format_the_double(result.serecom_score);
				line += "\t" + format_the_double(result.popularity_score);
				line += "\t" + result.resultURL + "\n";
				writer.write(line);
				count++;
				// if(count==20)break;
			}
			writer.close();
			System.out.println("Scores (ctx) saved successfully");
		} catch (Exception exc) {
			exc.printStackTrace();
		}*/
	}
	//saving content_serecom score
	public void save_the_result_score_cts(String fileName)
		{
			// code for saving the component scores
			/*try {
				//String baseFolder="D:/My MSc/CMPT 811/SurfClipse Tool Demo/data/results/cts";
				String baseFolder=StaticData.Base_Directory+"/results/cts";
				FileWriter writer = new FileWriter(new File(baseFolder+"/"+fileName));
				String header = "TotalScore\tconten\tcontext\tserecom\tpopularity\tURL \n";
				writer.write(header);
				int count = 0;
				for (Result result : this.Final_Results) {
					String line = format_the_double(result.totalScore_content_serecom) + "";
					line += "\t" + format_the_double(result.content_score);
					line += "\t" + format_the_double(result.context_score);
					line += "\t" + format_the_double(result.serecom_score);
					line += "\t" + format_the_double(result.popularity_score);
					line += "\t" + result.resultURL + "\n";
					writer.write(line);
					count++;
					// if(count==20)break;
				}
				writer.close();
				System.out.println("Scores (cts) saved successfully");
			} catch (Exception exc) {
				exc.printStackTrace();
			}*/
		}
		
	public void save_the_result_score_ctp(String fileName)
		{
			// code for saving the component scores
			/*try {
				//String baseFolder="D:/My MSc/CMPT 811/SurfClipse Tool Demo/data/results/ctp";
				String baseFolder=StaticData.Base_Directory+"/results/ctp";
				FileWriter writer = new FileWriter(new File(baseFolder+"/"+fileName));
				String header = "TotalScore\tconten\tcontext\tserecom\tpopularity\tURL \n";
				writer.write(header);
				int count = 0;
				for (Result result : this.Final_Results) {
					String line = format_the_double(result.totalScore_content_popularity) + "";
					line += "\t" + format_the_double(result.content_score);
					line += "\t" + format_the_double(result.context_score);
					line += "\t" + format_the_double(result.serecom_score);
					line += "\t" + format_the_double(result.popularity_score);
					line += "\t" + result.resultURL + "\n";
					writer.write(line);
					count++;
					// if(count==20)break;
				}
				writer.close();
				System.out.println("Scores (ctp) saved successfully");
			} catch (Exception exc) {
				exc.printStackTrace();
			}*/
		}
	
	public void save_the_result_score_ctxp(String fileName)
		{
			// code for saving the component scores
			try {
				//String baseFolder="D:/My MSc/CMPT 811/SurfClipse Tool Demo/data/results/ctxp";
				String baseFolder=StaticData.Base_Directory+"/results/ctxp";
				FileWriter writer = new FileWriter(new File(baseFolder+"/"+fileName));
				String header = "TotalScore\tcontent\tcontext\tpopularity\tURL \n";
				writer.write(header);
				int count = 0;
				for (Result result : this.Final_Results) {
					String line = format_the_double(result.totalScore_content_context_popularity) + "";
					line += "\t" + format_the_double(result.content_score);
					line += "\t" + format_the_double(result.context_score);
					line += "\t" + format_the_double(result.popularity_score);
					line += "\t" + result.resultURL + "\n";
					writer.write(line);
					count++;
					// if(count==20)break;
				}
				writer.close();
				System.out.println("Scores (ctxp) saved successfully");
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
		
	public void save_the_result_score_ctxs(String fileName)
		{
			// code for saving the component scores
			/*try {
				//String baseFolder="D:/My MSc/CMPT 811/SurfClipse Tool Demo/data/results/ctxs";
				String baseFolder=StaticData.Base_Directory+"/results/ctxs";
				FileWriter writer = new FileWriter(new File(baseFolder+"/"+fileName));
				String header = "TotalScore\tconten\tcontext\tserecom\tpopularity\tURL \n";
				writer.write(header);
				int count = 0;
				for (Result result : this.Final_Results) {
					String line = format_the_double(result.totalScore_content_context_serecom) + "";
					line += "\t" + format_the_double(result.content_score);
					line += "\t" + format_the_double(result.context_score);
					line += "\t" + format_the_double(result.serecom_score);
					line += "\t" + format_the_double(result.popularity_score);
					line += "\t" + result.resultURL + "\n";
					writer.write(line);
					count++;
					// if(count==20)break;
				}
				writer.close();
				System.out.println("Scores (ctxs) saved successfully");
			} catch (Exception exc) {
				exc.printStackTrace();
			}*/
		}
	
	public void save_the_result_score_ctxps(String fileName)
		{
			// code for saving the component scores
			/*try {
				//String baseFolder="D:/My MSc/CMPT 811/SurfClipse Tool Demo/data/results/ctxps";
				String baseFolder=StaticData.Base_Directory+"/results/ctxps";
				FileWriter writer = new FileWriter(new File(baseFolder+"/"+fileName));
				String header = "TotalScore\tconten\tcontext\tserecom\tpopularity\tURL \n";
				writer.write(header);
				int count = 0;
				for (Result result : this.Final_Results) {
					String line = format_the_double(result.totalScore_content_context_serecom_popularity) + "";
					line += "\t" + format_the_double(result.content_score);
					line += "\t" + format_the_double(result.context_score);
					line += "\t" + format_the_double(result.serecom_score);
					line += "\t" + format_the_double(result.popularity_score);
					line += "\t" + result.resultURL + "\n";
					writer.write(line);
					count++;
					// if(count==20)break;
				}
				writer.close();
				System.out.println("Scores (ctxps) saved successfully");
			} catch (Exception exc) {
				exc.printStackTrace();
			}*/
		}
		
	public void save_the_details_result(String fileName) {
		// code for saving the result details
		/*try {
			//String baseFolder="D:/My MSc/CMPT 811/SurfClipse Tool Demo/data/results/metrics";
			String baseFolder=StaticData.Base_Directory+"/results/metrics";
			FileWriter writer = new FileWriter(new File(baseFolder+"/"+fileName));
			String header="TotalScore\tSEWeight\tTitleMatching\tStackTrace\tCodeContext\tAlexaCompete\tSOVote\tTop10\tURL\n";
			writer.write(header);
			
			int count=0;
			for (Result result : this.Final_Results) {
				String line="";
				line=format_the_double(result.totalScore_content_context_serecom_popularity)+"";
				line+="\t"+format_the_double(result.SEWeight);
				line+="\t"+format_the_double(result.titleMatchScore);
				line+="\t"+format_the_double(result.stackTraceMatchScore);
				line+="\t"+format_the_double(result.sourceContextMatchScore);
				line+="\t"+format_the_double(result.AlexaCompeteRankScore);
				line+="\t"+format_the_double(result.SOVoteScore);
				line+="\t"+format_the_double(result.topTenScore);
				line+="\t"+format_the_double(result.pageRankScore);
				line+="\t"+result.resultURL+"\n";
				writer.write(line);
				count++;
				//if(count==20)break;
			}
			writer.close();
			System.out.println("Metrics are saved successfully.");
		} catch (Exception exc) {
			exc.printStackTrace();
		}*/
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
