package com.example.fil_rouge

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_my_first.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MyFirstFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MyFirstFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MyFirstFragment : Fragment() {
    var totalLog = ""
    override fun onAttach(context: Context) {
        super.onAttach(context)
        showLog("On Attach")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showLog("On Create")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        showLog("On CreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_first, container, false)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showLog("On Activity Created")
    }
    override fun onStart() {
        super.onStart()
        showLog("On Start")
    }
    override fun onResume() {
        super.onResume()
        showLog("On Resume")
        txtMyFirstFragment.setText(totalLog)
    }
    override fun onPause() {
        super.onPause()
        showLog("On Pause")
    }
    override fun onStop() {
        super.onStop()
        showLog("On Stop")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Toast.makeText(this.context,"On DestroyView", Toast.LENGTH_LONG).show()
    }
    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this.context,"On Destroy", Toast.LENGTH_LONG).show()
    }
    override fun onDetach() {
        super.onDetach()
        Toast.makeText(this.context,"On Detach", Toast.LENGTH_LONG).show()
    }
    private fun showLog(msg: String)
    {
        totalLog += msg + "\n"
    }
}
