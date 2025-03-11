import React, { Component } from 'react';
import $ from 'jquery';
import EditorDemo from './components/test/EditorDemo';
import Navbar from './components/Navbar';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import Home from './components/Home';
import Login from './components/Login';
import NotFound from './components/NotFound';
import ProblemManage from './components/ProblemManage';
import ProblemSet from './components/ProblemSet';
import Register from './components/Register';
import SetManage from './components/SetManage';
import UserProfile from './components/UserProfile';
import { connect } from 'react-redux';
import { jwtDecode } from "jwt-decode";
import ACTIONS from './redux/actions';
import ProgrammingEditor from './components/test/ProgrammingEditor';
import MarkdownEditor from './components/test/MarkdownEditor';


import BACKEND_ADDRESS_URL from "./components/config/BackendAddressURLConfig";


import UserManage from './components/UserManage';
import ObjectiveProblemManage from './components/problem/ObjectiveProblemManage';
import ObjectiveProblemCreate from './components/problem/ObjectiveProblemCreate';
import ObjectiveProblemPreview from './components/problem/ObjectiveProblemPreview';
import ProgrammingManage from './components/problem/ProgrammingManage';
import ProgrammingCreate from './components/problem/ProgrammingCreate';
import ProgrammingPreview from './components/problem/ProgrammingPreview';
import AssignmentManage from './components/set/AssignmentManage';
import ExamManage from './components/set/ExamManage';
import SetPreview from './components/set/SetPreview';
import ProblemSetAll from './components/problem_set/ProblemSetAll';
import ProblemSetStudent from './components/problem_set/student/ProblemSetStudent';
import ProblemSetObjectiveProblemStudent from './components/problem_set/student/ProblemSetObjectiveProblemStudent';
import ProblemSetProgrammingStudent from './components/problem_set/student/ProblemSetProgrammingStudent';



class App extends Component {
    state = {}

    componentDidMount() {
        const token = localStorage.getItem("token");
        // console.log(token);
        if (token !== null) {
            const decoded = jwtDecode(token);
            console.log(decoded);
            const now = new Date();
            if (now + 1 * 60 * 1000 > decoded.exp) {
                console.log("token expired");
                localStorage.removeItem("token");
            }
            $.ajax({
                url: BACKEND_ADDRESS_URL + "/user/account/info/",
                type: "GET",
                headers: {
                    Authorization: "Bearer " + token
                },
                success: (resp) => {
                    if (resp.error_message === "success") {
                        const name = resp.name;
                        const permission = parseInt(resp.permission);
                        const user_id = parseInt(resp.user_id);
                        const username = resp.username;
                        this.props.update_user({
                            user_id: user_id,
                            username: username,
                            name: name,
                            permission: permission
                        });
                        this.props.update_token({ token: token });
                        this.props.update_user({
                            user_id: user_id,
                            username: username,
                            name: name,
                            permission: permission
                        });
                    } else {
                        console.log(resp);
                        localStorage.removeItem("token");
                    }
                },
                error: (resp) => {
                    console.log(resp);
                    localStorage.removeItem("token");
                }
            });
        }
    }

    render() {
        return (
            <BrowserRouter>
                <Navbar />
                <Routes>
                    <Route path='/' element={<Home />} />
                    <Route path='/editor_demo/' element={<EditorDemo />} />
                    <Route path='/login/' element={<Login />} />
                    <Route path='/markdown_editor_demo/' element={<MarkdownEditor />} />
                    <Route path='/404/' element={<NotFound />} />
                    <Route path='/programming_editor_demo/' element={<ProgrammingEditor />} />
                    <Route path='/register/' element={<Register />} />

                    {/* Problem Management */}
                    <Route path='/problem_manage/' element={<ProblemManage />} />
                    <Route path='/problem_manage/objective_problem_manage/' element={<ObjectiveProblemManage />} />
                    <Route path='/problem_manage/objective_problem_manage/create/' element={<ObjectiveProblemCreate />} />
                    <Route path='/problem_manage/objective_problem_manage/:objective_problem_id/' element={<ObjectiveProblemPreview />} />
                    <Route path='/problem_manage/programming_manage/' element={<ProgrammingManage />} />
                    <Route path='/problem_manage/programming_manage/create/' element={<ProgrammingCreate />} />
                    <Route path='/problem_manage/programming_manage/:programming_id/' element={<ProgrammingPreview />} />

                    {/* My Problem Set */}
                    <Route path='/problem_set/student_view/' element={<ProblemSet />} />
                    <Route path='/problem_set/student_view/all/' element={<ProblemSetAll />} />
                    <Route path='/problem_set/student_view/one/:problem_set_id/' element={<ProblemSetStudent />} />
                    <Route path='/problem_set/student_view/objective_problem/:problem_set_id/:objective_problem_id/' element={<ProblemSetObjectiveProblemStudent />} />
                    <Route path='/problem_set/student_view/programming/:problem_set_id/:programming_id/' element={<ProblemSetProgrammingStudent />} />


                    {/* Problem Set Management */}
                    <Route path='/set_manage/' element={<SetManage />} />
                    <Route path='/set_manage/assignment/' element={<AssignmentManage />} />
                    <Route path='/set_manage/exam/' element={<ExamManage />} />
                    <Route path='/set_manage/assignment/:problem_set_id/' element={<SetPreview />} />
                    <Route path='/set_manage/exam/:problem_set_id/' element={<SetPreview />} />


                    {/* User Account System */}
                    <Route path='/user_manage/' element={<UserManage />} />
                    <Route path='/user_profile/' element={<UserProfile />} />
                    <Route path="/*" element={<Navigate replace to="/404/" />} />
                </Routes>
            </BrowserRouter>
        );
    }
}

const mapStateToProps = (state, props) => {
    return {
        ...props,
        user_id: state.user_id,
        username: state.username,
        name: state.name,
        permission: state.permission,
        token: state.token,
        is_login: state.is_login,
    };
};

const mapDispatchToProps = {
    update_user: (data) => {
        return {
            type: ACTIONS.UPDATE_USER,
            user_id: data.user_id,
            username: data.username,
            name: data.name,
            permission: data.permission
        };
    },
    update_token: (data) => {
        return {
            type: ACTIONS.UPDATE_TOKEN,
            token: data.token,
        }
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(App);