<?php

namespace App\Http\Controllers;

use App\Models\Pages;
use Illuminate\Http\Request;

class PagesController extends Controller
{
    public function privacyPolicy()
    {
        $data = Pages::first();
        return view('pages.privacyPolicy', [
            'data' => $data->privacy
        ]);
    }
   
    public function termsOfUse()
    {
        $data = Pages::first();
        return view('pages.termsOfUse', [
            'data' => $data->termsofuse
        ]);
    }


    public function viewTerms()
    {
        $data = Pages::first();
        return view('pages.viewTerms', ['data' => $data->termsofuse]);
    }

    public function updatePrivacy(Request $request)
    {
        $data = Pages::first();
        $data->privacy = $request->content;
        $data->save();

        return  json_encode(['status' => true, 'message' => "update successful"]);
    }

    public function updateTerms(Request $request)
    {
        $data = Pages::first();
        $data->termsofuse = $request->content;
        $data->save();

        return  json_encode(['status' => true, 'message' => "update successful"]);
    }

    public function viewPrivacy()
    {
        $data = Pages::first();
        return view('pages.viewPrivacy', ['data' => $data->privacy]);
    }

    public function addContentForm(Request $request)
    {
        $privacy = Pages::first();
        if ($privacy) {
            if ($request->has('privacy')) {
                $privacy->privacy = $request->privacy;
            }
        }
        $privacy->save();

        return response()->json([
            'status' => true,
            'message' => 'privacy Policy Added',
            'data' => $privacy,
        ]); 
    }

    public function addTermsForm(Request $request)
    {
        $terms = Pages::first();
        if ($terms) {
            if ($request->has('termsofuse')) {
                $terms->termsofuse = $request->termsofuse;
            }
        }
        $terms->save();

        return response()->json([
            'status' => true,
            'message' => 'Terms Policy Added',
            'data' => $terms,
        ]); 
    }
}
