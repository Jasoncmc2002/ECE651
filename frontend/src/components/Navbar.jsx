import React, { Component } from 'react';
import { Link, NavLink } from 'react-router-dom';
import Brand from '../images/icon.png';
import { connect } from 'react-redux';
import ACTIONS from '../redux/actions';

class Navbar extends Component {
    state = {}

    handleAccountRender = () => {
        if (this.props.is_login) {
            return (
                <ul className="navbar-nav ms-auto mb-2 mb-lg-0">
                    <li className="nav-item">
                        <NavLink className="nav-link" to="/user_profile/">{this.props.name}</NavLink>
                    </li>
                    <li className="nav-item">
                        <Link className="nav-link" to="/" onClick={() => this.props.logout()}>Sign Out</Link>
                    </li>
                </ul>
            );
        } else {
            return (
                <ul className="navbar-nav ms-auto mb-2 mb-lg-0">
                    <li className="nav-item">
                        <NavLink className="nav-link" to="/login/">Sign In</NavLink>
                    </li>
                    <li className="nav-item">
                        <NavLink className="nav-link" to="/register">Register</NavLink>
                    </li>
                </ul>
            );
        }
    }

    handlePermissionRender = () => {
        if (this.props.permission === 1) {
            return (
                <React.Fragment>
                    {/* <li className="nav-item">
                        <NavLink className="nav-link" to="/problem_manage/">试题管理</NavLink>
                    </li> */}

                    <li className="nav-item dropdown">
                        <NavLink className="nav-link dropdown-toggle" to="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            Problem Management
                        </NavLink>
                        <ul className="dropdown-menu">
                            <li><NavLink className="dropdown-item" to="/problem_manage/objective_problem_manage/">Objective Problems</NavLink></li>
                            <li><NavLink className="dropdown-item" to="/problem_manage/programming_manage/">Programming Problems</NavLink></li>
                        </ul>
                    </li>
                    <li className="nav-item dropdown">
                        <NavLink className="nav-link dropdown-toggle" to="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            Problem Set Management
                        </NavLink>
                        <ul className="dropdown-menu">
                            <li><NavLink className="dropdown-item" to="/set_manage/assignment/">Assignments</NavLink></li>
                            <li><NavLink className="dropdown-item" to="/set_manage/exam/">Exams</NavLink></li>
                        </ul>
                    </li>
                </React.Fragment>
            );
        } else if (this.props.permission > 1) {
            return (
                <React.Fragment>
                    {/* <li className="nav-item">
                        <NavLink className="nav-link" to="/problem_manage/">试题管理</NavLink>
                    </li> */}

                    <li className="nav-item dropdown">
                        <NavLink className="nav-link dropdown-toggle" to="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            Problem Management
                        </NavLink>
                        <ul className="dropdown-menu">
                            <li><NavLink className="dropdown-item" to="/problem_manage/objective_problem_manage/">Objective Problems</NavLink></li>
                            <li><NavLink className="dropdown-item" to="/problem_manage/programming_manage/">Programming Problems</NavLink></li>
                        </ul>
                    </li>
                    <li className="nav-item dropdown">
                        <NavLink className="nav-link dropdown-toggle" to="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            Problem Set Management
                        </NavLink>
                        <ul className="dropdown-menu">
                            <li><NavLink className="dropdown-item" to="/set_manage/assignment/">Assignments</NavLink></li>
                            <li><NavLink className="dropdown-item" to="/set_manage/exam/">Exams</NavLink></li>
                        </ul>
                    </li>
                    <li className="nav-item">
                        <NavLink className="nav-link" to="/user_manage/">User Management</NavLink>
                    </li>
                </React.Fragment>
            );
        }
    }

    handleDeveloperRender = () => {
        if (this.props.permission > 1) {
            return (
                <li className="nav-item dropdown">
                    <NavLink className="nav-link dropdown-toggle" to="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        Developer
                    </NavLink>
                    <ul className="dropdown-menu">
                        <li><NavLink className="dropdown-item" to="/404/">404</NavLink></li>
                        <li><NavLink className="dropdown-item" to="/user_profile/">User Profile</NavLink></li>
                        <li><NavLink className="dropdown-item" to="/editor_demo/">Editor Demo</NavLink></li>
                        <li><NavLink className="dropdown-item" to="/programming_editor_demo/">Programming Editor Demo</NavLink></li>
                        <li><NavLink className="dropdown-item" to="/markdown_editor_demo/">Markdown Editor Demo</NavLink></li>
                    </ul>
                </li>
            );
        }
    }

    render() {
        if (this.props.is_login) {
            return (
                <nav className="navbar navbar-expand-lg sticky-top bg-dark" data-bs-theme="dark">
                    <div className="container-fluid">
                        <NavLink className="navbar-brand" to="/">
                            <img src={Brand} className='me-2' alt="Brand" width="30" height="30"></img>
                            Python Programming Platform
                        </NavLink>
                        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText" aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
                            <span className="navbar-toggler-icon"></span>
                        </button>
                        <div className="collapse navbar-collapse" id="navbarText">
                            <ul className="navbar-nav me-auto mb-2 mb-lg-0">
                                <li className="nav-item">
                                    <NavLink className="nav-link" to="/">Home</NavLink>
                                </li>
                                <li className="nav-item">
                                    <NavLink className="nav-link" to="/problem_set/student_view/">My Problem Set</NavLink>
                                </li>
                                {this.handlePermissionRender()}
                                {this.handleDeveloperRender()}
                            </ul>
                            {this.handleAccountRender()}
                        </div>
                    </div>
                </nav>
            );
        } else {
            return (
                <nav className="navbar navbar-expand-lg sticky-top bg-dark" data-bs-theme="dark">
                    <div className="container-xxl justify-content-md-center">
                        <NavLink className="navbar-brand" to="/">
                            <img src={Brand} className='me-2' alt="Brand" width="30" height="30"></img>
                            Python Programming Platform
                        </NavLink>
                    </div>
                </nav>
            );
        }
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
    logout: () => {
        return {
            type: ACTIONS.LOGOUT
        }
    }
};

export default connect(mapStateToProps, mapDispatchToProps)(Navbar);